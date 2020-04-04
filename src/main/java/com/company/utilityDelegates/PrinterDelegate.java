package com.company.utilityDelegates;

import com.company.managerDAO.ManagerDAO;
import com.company.managerDAO.UserRowDTO;
import com.company.userAuthDelegates.UserAuthManager;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

/**
 * Uses bookingManagerImpl and availabilityManagerImpl to retrieve relevant user information and print their
 * outputs.
 */
public class PrinterDelegate {

    // Authentication implementation responsible for transferring control flow to either login or registration.
    @NotNull private final UserAuthManager userAuthImpl;
    // Used to format the date and time in the following format: "MM/dd HH:mm".
    @NotNull private final DateFormat sdfDateTimeFormatter;
    // Implements the ManagerDAO interface and executes SQL queries to modify the user bookings MySQL table.
    @NotNull private final ManagerDAO bookingManagerImpl;
    // Implements the ManagerDAO interface and executes SQL queries to modify the user availabilities MySQL table.
    @NotNull private final ManagerDAO availabilityManagerImpl;

    public PrinterDelegate(
            @NotNull UserAuthManager userAuthImpl,
            @NotNull ManagerDAO bookingManagerImpl,
            @NotNull ManagerDAO availabilityManagerImpl) {
        this.userAuthImpl = userAuthImpl;
        this.bookingManagerImpl = bookingManagerImpl;
        this.availabilityManagerImpl = availabilityManagerImpl;
        sdfDateTimeFormatter = new SimpleDateFormat(Constants.DATE_TIME_PRINT_FORMAT);
    }

    /**
     *  Prints all of the current bookings within the bookings MySQL table. Makes a call to getAllUserRows() using
     *  bookingManagerImpl and traverses the returned Set to print all current bookings for each day.
     *  Keeps track of the most recently printed date to group all same dates together for readability.
     */
    public void printCurrentBookingsHelper() {
        UserRowDTO printCurrentBookingsDTO = new UserRowDTO();
        printCurrentBookingsDTO.setUserName(userAuthImpl.getCurrentUser());
        Set<UserRowDTO> currentBookings = bookingManagerImpl.getAllUserRows(printCurrentBookingsDTO);

        if (currentBookings == null) {
            System.out.println(Constants.NO_CURRENT_BOOKINGS);
            return;
        }
        System.out.println(Constants.PRINTING_BOOKINGS);
        int bookingCount = 1;
        Timestamp prevDate = null;
        // Used to determine whether the current row's date differs from the previous row's one. Allows us to group
        // all bookings of the same date together in the output.
        boolean isDifferentDate = false;
        for (UserRowDTO currentBookingRow : currentBookings) {
            String retrievedCompanyName = currentBookingRow.getCompanyName();
            Timestamp retrievedBookingStartTime = currentBookingRow.getStartTime();
            Timestamp retrievedBookingEndTime = currentBookingRow.getEndTime();
            int retrievedBookingDay, prevBookingDay;
            Month retrievedBookingMonth, prevBookingMonth;
            if (prevDate != null) {
                retrievedBookingDay = retrievedBookingStartTime.toLocalDateTime().getDayOfMonth();
                prevBookingDay = prevDate.toLocalDateTime().getDayOfMonth();
                retrievedBookingMonth = retrievedBookingStartTime.toLocalDateTime().getMonth();
                prevBookingMonth = prevDate.toLocalDateTime().getMonth();

                if ((retrievedBookingDay != prevBookingDay) || (retrievedBookingMonth != prevBookingMonth)) {
                    isDifferentDate = true;
                }
            }
            prevDate = retrievedBookingStartTime;
            // Retrieves booking date and time into a String[] as such: [date (MM/dd), time (HH:mm)].
            String[] formattedStartTime = sdfDateTimeFormatter.format(retrievedBookingStartTime).
                    split(Constants.SPACE);
            String[] formattedEndTime = sdfDateTimeFormatter.format(retrievedBookingEndTime).split(Constants.SPACE);
            if (isDifferentDate) {
                System.out.println();
            }
            System.out.println(String.format(Constants.BOOKING_PRINT_LINE, bookingCount, retrievedCompanyName,
                    formattedStartTime[0], formattedStartTime[1], formattedEndTime[1]));
            bookingCount++;
            isDifferentDate = false;
        }
        System.out.println();
    }

    /**
     *  Prints all of the current bookings within the bookings MySQL table. Makes a call to getAllUserRows() using
     *  availabilityManagerImpl and traverses the returned Set to print all current availabilities for each day.
     *  Keeps track of the most recently printed date to group all same dates together for readability.
     */
    public void printCurrentAvailabilityHelper() {
        UserRowDTO printCurrentOpeningsDTO = new UserRowDTO();
        printCurrentOpeningsDTO.setUserName(userAuthImpl.getCurrentUser());
        Set<UserRowDTO> currentOpeningsRows = availabilityManagerImpl.getAllUserRows(printCurrentOpeningsDTO);

        if (currentOpeningsRows == null) {
            System.out.println(Constants.NO_CURRENT_AVAILABILITY);
            return;
        }
        System.out.println(Constants.PRINTING_AVAILABILITY);
        int availabilityCount = 1;
        Timestamp prevDate = null;
        // Used to determine whether the current row's date differs from the previous row's one. Allows us to group
        // all bookings of the same date together in the output.
        boolean isDifferentDate = false;
        for (UserRowDTO currentOpeningRow : currentOpeningsRows) {
            Timestamp retrievedAvailabilityStartTime = currentOpeningRow.getStartTime();
            Timestamp retrievedAvailabilityEndTime = currentOpeningRow.getEndTime();
            int retrievedBookingDay, prevBookingDay;
            Month retrievedBookingMonth, prevBookingMonth;
            if (prevDate != null) {
                retrievedBookingDay = retrievedAvailabilityStartTime.toLocalDateTime().getDayOfMonth();
                prevBookingDay = prevDate.toLocalDateTime().getDayOfMonth();
                retrievedBookingMonth = retrievedAvailabilityEndTime.toLocalDateTime().getMonth();
                prevBookingMonth = prevDate.toLocalDateTime().getMonth();

                if ((retrievedBookingDay != prevBookingDay) || (retrievedBookingMonth != prevBookingMonth)) {
                    isDifferentDate = true;
                }
            }
            prevDate = retrievedAvailabilityEndTime;
            // Retrieves booking date and time into a String[] as such: [date (MM/dd), time (HH:mm)].
            String[] formattedStartTime = sdfDateTimeFormatter.format(retrievedAvailabilityStartTime).
                    split(Constants.SPACE);
            String[] formattedEndTime = sdfDateTimeFormatter.format(retrievedAvailabilityEndTime).
                    split(Constants.SPACE);
            if (isDifferentDate) {
                System.out.println();
            }
            System.out.println(String.format(Constants.AVAILABILITY_PRINT_LINE, availabilityCount,
                    formattedStartTime[0], formattedStartTime[1], formattedEndTime[1]));
            availabilityCount++;
            isDifferentDate = false;
        }
        System.out.println();
    }
}
