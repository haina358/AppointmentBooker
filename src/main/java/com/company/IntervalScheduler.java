package com.company;

import com.company.exceptions.MaxUsernameLengthViolationException;
import com.company.managerDAO.ManagerDAO;
import com.company.managerDAO.UserRowDTO;
import com.company.userAuthDelegates.UserAuthManager;
import com.company.utilityDelegates.constants.Constants;
import com.company.utilityDelegates.DateTimeVerificationDelegate;
import com.company.utilityDelegates.IntervalSchedulerDelegate;
import com.company.utilityDelegates.PrinterDelegate;
import org.jetbrains.annotations.NotNull;

import java.io.Console;
import java.sql.*;
import java.util.*;

/**
 * IntervalScheduler is an implementation of a dynamic calendar. Users can input their availability for a given
 * day, add and delete calendar bookings, and print all their current bookings.
 *
 * Availability for a particular day is dynamically updated as bookings are added by the user. If the user attempts to
 * add conflicting bookings, those bookings will not be added and the user will be notified.
 *
 * Users are prompted to either enter a date to begin adding a booking, <Bookings> to view all current bookings,
 * <Openings> to view current openings, <Add> to input availability for a particular date, or <Delete> to delete a
 * particular booking.
 */
public class IntervalScheduler {

    // Uses bookingManagerImpl and availabilityManagerImpl to retrieve relevant user information and print their
    // outputs.
    @NotNull private final PrinterDelegate printerDelegate;
    // Handles the verification of input dates and times to ensure that they conform to the specified formats.
    @NotNull private final DateTimeVerificationDelegate dateTimeVerificationDelegate;
    @NotNull private final Scanner sc;
    // Facilitates user input formatting and various operations outside the scope of IntervalScheduler.
    @NotNull private final IntervalSchedulerDelegate ISDelegate;
    // Authentication Implementation primarily responsible for transferring control flow to either login or
    // registration. userAuthImpl also facilitates user logout as well as retrieval of the currently logged in user.
    @NotNull private final UserAuthManager userAuthManager;
    // An implementation of the ManagerDAO interface responsible for executing SQL queries on the MySQL openings table.
    @NotNull private final ManagerDAO availabilityManagerImpl;

    public IntervalScheduler(@NotNull IntervalSchedulerDelegate intervalSchedulerDelegate,
                             @NotNull UserAuthManager userAuthManager,
                             @NotNull DateTimeVerificationDelegate dateTimeVerificationDelegate,
                             @NotNull Scanner sc,
                             @NotNull ManagerDAO availabilityManagerImpl,
                             @NotNull PrinterDelegate printerDelegate) {
        this.ISDelegate = intervalSchedulerDelegate;
        this.userAuthManager = userAuthManager;
        this.dateTimeVerificationDelegate = dateTimeVerificationDelegate;
        this.sc = sc;
        this.availabilityManagerImpl = availabilityManagerImpl;
        this.printerDelegate = printerDelegate;
    }

    /**
     * Entry point into the program. Begins by providing the user with a list of options and calls the respective
     * methods based upon the user input.
     */
    public void start() {
        // If the user isn't logged in, calls promptLoginAndRegister() to prompt them to either login or register.
        if (userAuthManager.getCurrentUser() == null) {
            promptLoginAndRegister();
        }
        String input;
        //noinspection InfiniteLoopStatement
        while (true) {
            for (String mainMenuOption : Constants.MAIN_MENU_OPTIONS) {
                System.out.println(mainMenuOption);
            }
            input = sc.nextLine().toLowerCase().trim();
            switch (input) {
                case Constants.INPUT_BOOKINGS:
                    printerDelegate.printCurrentBookingsHelper();
                    break;
                case Constants.INPUT_OPENINGS:
                    printerDelegate.printCurrentAvailabilityHelper();
                    break;
                case Constants.INPUT_DELETE:
                    deleteBookingPrompter();
                    break;
                case Constants.INPUT_ADD:
                    System.out.println();
                    inputAvailabilityHelper();
                    break;
                case Constants.INPUT_LOGOUT:
                    System.out.println();
                    userAuthManager.logOut();
                    promptLoginAndRegister();
                    break;
                default:
                    if (dateTimeVerificationDelegate.isValidDate(input)) {
                        durationTimeHelper(input);
                    } else {
                        System.out.println(Constants.INVALID_INPUT);
                    }
                    break;
            }
        }
    }

    /**
     * Prompts the user to either login or register. Input is then checked to determine whether the user should undergo
     * the login or registration flow.
     */
    public void promptLoginAndRegister() {
        String input;
        Console console = System.console();
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.print(Constants.PROMPT_LOGIN_REGISTER);
            input = sc.nextLine().toLowerCase().trim();
            switch (input) {
                // Prompts for credentials, then delegates login work to LoginUserDelegate via userAuthDelegate.
                case Constants.INPUT_LOGIN:
                    String usernameLogin, passwordLogin;
                    System.out.print(Constants.PROMPT_USERNAME);
                    usernameLogin = sc.nextLine().trim();
                    passwordLogin = new String(console.readPassword(Constants.PROMPT_PASSWORD));
                    boolean isLoginSuccessful = userAuthManager.login(usernameLogin, passwordLogin);
                    if (isLoginSuccessful) {
                        System.out.println(String.format(Constants.SUCCESSFUL_LOGIN, usernameLogin));
                        // Redirects to the main menu.
                        start();
                    } else {
                        System.out.println(Constants.INVALID_LOGIN);
                    }
                    break;
                // Prompts for credentials, then delegates registration work to RegisterUserDelegate
                // via userAuthDelegate.
                case Constants.INPUT_REGISTER:
                    String usernameRegister, passwordRegister, passwordRegisterConfirm;
                    boolean isRegistrationSuccessful;
                    System.out.print(Constants.PROMPT_USERNAME);
                    usernameRegister = sc.nextLine().trim();
                    passwordRegister = new String(console.readPassword(Constants.PROMPT_PASSWORD));
                    passwordRegisterConfirm = new String(console.readPassword(Constants.PROMPT_CONFIRM_PASSWORD));
                    if (!passwordRegister.equals(passwordRegisterConfirm)) {
                        System.out.println(Constants.PASSWORD_MISMATCH);
                        continue;
                    }
                    try {
                        isRegistrationSuccessful = userAuthManager.register(usernameRegister, passwordRegister);
                    } catch (MaxUsernameLengthViolationException e) {
                        System.out.println(e);
                        continue;
                    }
                    if (isRegistrationSuccessful) {
                        System.out.println(Constants.SUCCESSFUL_REGISTRATION);
                        // Redirect to main menu.
                        start();
                    } else {
                        System.out.println(String.format(Constants.REGISTRATION_FAILED, usernameRegister));
                    }
                    break;
                default:
                    System.out.println(Constants.INVALID_INPUT);
                    break;
            }
        }
    }

    /**
     * Prompts for the necessary information to delete a booking, validates the input using
     * dateTimeVerificationDelegate, and delegates the deletion to ISDelegate.
     */
    private void deleteBookingPrompter() {
        String input;
        String[] deleteInput = new String[4];
        //noinspection InfiniteLoopStatement
        while (true) {
            for (int i = 0; i < Constants.DELETE_PROMPTS.length; i++) {
                System.out.print(Constants.DELETE_PROMPTS[i]);
                deleteInput[i] = sc.nextLine().trim();
                checkInputForCancel(deleteInput[i]);
            }
            if (!dateTimeVerificationDelegate.isValidTimeInterval(deleteInput[2], deleteInput[3]) ||
                    !dateTimeVerificationDelegate.isValidDate(deleteInput[0])) {
                System.out.println(Constants.INVALID_INPUT_DELETE);
                continue;
            }
            System.out.print(String.format(Constants.PROMPT_CONFIRM_DELETE_BOOKING, deleteInput[1], deleteInput[0],
                    deleteInput[2], deleteInput[3]));
            input = sc.nextLine().toLowerCase();
            if (input.equals(Constants.INPUT_YES)) {
                boolean couldDeleteBooking = ISDelegate.deleteBookingHelper(deleteInput);
                if (couldDeleteBooking) {
                    System.out.println(Constants.BOOKING_DELETED);
                } else {
                    System.out.println(String.format(Constants.BOOKING_DELETE_FAILURE, deleteInput[1], deleteInput[0],
                            deleteInput[2], deleteInput[3]));
                }
            }
            start();
        }
    }

    /**
     * Prompts for a company, startTime, and endTime of a booking. durationTimeHelper() also validates the the input
     * before calling addBookingHelper() to add the booking.
     *
     * @param date of the booking
     */
    private void durationTimeHelper(String date) {
        String startTime, endTime, companyName;
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.print(Constants.PROMPT_WHICH_COMPANY);
            companyName = sc.nextLine().trim();
            checkInputForCancel(companyName);
            System.out.print(String.format(Constants.PROMPT_BOOKING_START_TIME, companyName, date));
            startTime = sc.nextLine().trim();
            checkInputForCancel(startTime);
            if (!dateTimeVerificationDelegate.isValidTime(startTime)) {
                System.out.println(Constants.INVALID_START_TIME);
                continue;
            }
            System.out.print(String.format(Constants.PROMPT_BOOKING_END_TIME, companyName, date));
            endTime = sc.nextLine().trim();
            checkInputForCancel(endTime);
            if (!dateTimeVerificationDelegate.isValidTime(endTime)) {
                System.out.println(Constants.INVALID_END_TIME);
                continue;
            } else if (!dateTimeVerificationDelegate.isValidTimeInterval(startTime, endTime)) {
                System.out.println(String.format(Constants.INVALID_TIME_INTERVAL, startTime, endTime));
                continue;
            }
            addBookingHelper(date, startTime, endTime, companyName);
            start();
        }
    }

    /**
     * Calls couldAddNewBooking() to add the booking duration if possible.
     *
     * Otherwise, addBookingHelper() notifies the user that the booking could not be added and then calls
     * durationTimeHelper() to prompt for company, startTime, and endTime again.
     *
     * @param date of the booking
     * @param startTime of the booking in [startTime, endTime]
     * @param endTime of the booking in [startTime, endTime]
     * @param companyName is the name of the appointment being booked for
     */
    private void addBookingHelper(String date, String startTime, String endTime, String companyName) {
        while (!couldAddNewBooking(date, startTime, endTime, companyName)) {
            System.out.println(String.format(Constants.NO_AVAILABILITY, companyName, date, startTime, endTime));
            durationTimeHelper(date);
        }
        System.out.println(String.format(Constants.BOOKING_ADDED, companyName, date, startTime, endTime));
    }

    /**
     * Calls getAllUserRows() on availabilityManagerImpl to retrieve all of the user's current availability.
     * couldAddNewBooking() then uses ISDelegate to traverse that availability to determine if the booking could be
     * added.
     *
     * @param date of the booking
     * @param startTime of the booking [startTime, endTime]
     * @param endTime of the booking [startTime, endTime]
     * @param companyName is the name of the appointment being booked for
     * @return whether the booking duration was added for parameter date
     */
    private boolean couldAddNewBooking(String date, String startTime, String endTime, String companyName) {
        Timestamp[] bookingDuration = ISDelegate.formatDuration(startTime, endTime, date);
        Timestamp[] availabilityToBeRemoved;
        UserRowDTO doesAvailabilityExistDTO = new UserRowDTO();
        doesAvailabilityExistDTO.setUserName(userAuthManager.getCurrentUser());
        Set<UserRowDTO> currentOpeningsRows = availabilityManagerImpl.getAllUserRows(doesAvailabilityExistDTO);
        if (currentOpeningsRows == null) {
            System.out.println(String.format(Constants.NO_AVAILABILITY_ENTERED, date));
            start();
        }
        assert currentOpeningsRows != null;
        availabilityToBeRemoved = ISDelegate.
                traverseCurrentOpeningsAndSplitTimeBlockIfNecessary(currentOpeningsRows, bookingDuration);

        if (availabilityToBeRemoved != null) {
            ISDelegate.deleteUserAvailabilityToAddBookingInterval(availabilityToBeRemoved);
            return ISDelegate.insertBookingInterval(companyName, bookingDuration);
        } else {
            return false;
        }
    }

    /**
     * Allows users to input their availability for a given day. Prompts the user for a date, startTime, and endTime
     * for their availability. Validates user input, then creates a UserRowDTO to model the input before using
     * availabilityManagerImpl to extract the information from the UserRowDTO and execute the insertion into the MySQL
     * backend.
     */
    private void inputAvailabilityHelper() {
        String dateInput, availabilityStartTime, availabilityEndTime;
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.print(Constants.PROMPT_AVAILABILITY_DATE);
            dateInput = sc.nextLine().trim();
            checkInputForCancel(dateInput);
            if (!dateTimeVerificationDelegate.isValidDate(dateInput)) {
                System.out.println(Constants.INVALID_DATE);
                continue;
            }
            System.out.print(String.format(Constants.PROMPT_AVAILABILITY_START_TIME, dateInput));
            availabilityStartTime = sc.nextLine().trim();
            checkInputForCancel(availabilityStartTime);
            if (!dateTimeVerificationDelegate.isValidTime(availabilityStartTime)) {
                System.out.println(Constants.INVALID_START_TIME);
                continue;
            }
            System.out.print(String.format(Constants.PROMPT_AVAILABILITY_END_TIME, dateInput));
            availabilityEndTime = sc.nextLine().trim();
            checkInputForCancel(availabilityEndTime);
            if (!dateTimeVerificationDelegate.isValidTime(availabilityEndTime)) {
                System.out.println(Constants.INVALID_END_TIME);
                continue;
            } else if (!dateTimeVerificationDelegate.isValidTimeInterval(availabilityStartTime, availabilityEndTime)) {
                System.out.println(String.format(Constants.INVALID_TIME_INTERVAL, availabilityStartTime,
                        availabilityEndTime));
                continue;
            } else if (ISDelegate.doesOverlapWithBooking(dateInput, availabilityStartTime, availabilityEndTime)) {
                System.out.println(Constants.ADD_OPENING_FAILURE_1);
                continue;
            } else if (ISDelegate.doesOverlapWithOpening(dateInput, availabilityStartTime, availabilityEndTime)) {
                System.out.println(Constants.ADD_OPENING_FAILURE_2);
                continue;
            }
            Timestamp[] availabilityDur = ISDelegate.
                    formatDuration(availabilityStartTime, availabilityEndTime, dateInput);
            UserRowDTO insertIntoAvailabilitiesDTO = new UserRowDTO(userAuthManager.getCurrentUser(),
                    availabilityDur[0], availabilityDur[1]);
            availabilityManagerImpl.insertUserRow(insertIntoAvailabilitiesDTO);
            System.out.println(String.format(Constants.AVAILABILITY_ADDED, dateInput, availabilityStartTime,
                    availabilityEndTime));
        }
    }

    /**
     * Called whenever user input is received to determine whether control should return back to the main menu.
     *
     * @param input user input
     */
    private void checkInputForCancel(String input) {
        if (input.toLowerCase().equals(Constants.INPUT_CANCEL)) {
            System.out.println();
            start();
        }
    }
}
