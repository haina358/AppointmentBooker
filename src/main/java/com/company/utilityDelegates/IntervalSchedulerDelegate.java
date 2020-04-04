package com.company.utilityDelegates;

import com.company.managerDAO.AvailabilityManagerImpl;
import com.company.managerDAO.BookingManagerImpl;
import com.company.managerDAO.ManagerDAO;
import com.company.managerDAO.UserRowDTO;
import com.company.userAuthDelegates.UserAuthManager;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Delegate class to facilitate user input formatting and various operations outside the scope of IntervalScheduler.
 */
public class IntervalSchedulerDelegate {

    // Authentication Implementation primarily responsible for transferring control flow to either login or
    // registration. userAuthImpl also facilitates user logout as well as retrieval of the currently logged in user.
    @NotNull private final UserAuthManager userAuthManager;
    // An implementation of the ManagerDAO interface responsible for executing SQL queries on the MySQL bookings table.
    @NotNull private final ManagerDAO bookingManagerImpl;
    // An implementation of the ManagerDAO interface responsible for executing SQL queries on the MySQL openings table.
    @NotNull private final ManagerDAO availabilityManagerImpl;

    public IntervalSchedulerDelegate(@NotNull UserAuthManager userAuthManager,
                                     @NotNull ManagerDAO bookingManagerImpl,
                                     @NotNull ManagerDAO availabilityManagerImpl) {
        this.userAuthManager = userAuthManager;
        this.bookingManagerImpl = bookingManagerImpl;
        this.availabilityManagerImpl = availabilityManagerImpl;
    }

    /**
     * Deletes the booking corresponding to the values within the parameter inputArray and adds the time slot to the
     * user's current openings.
     * Returns false if there exists no booking for the provided duration.
     *
     * @param inputArray is a list of values as specified by the user to delete a booking
     * @return true if the booking was successfully deleted, false otherwise
     */
    public boolean deleteBookingHelper(@NotNull String[] inputArray) {
        boolean couldDeleteBooking;
        Timestamp[] bookingDurationToDelete = formatDuration(inputArray[2], inputArray[3], inputArray[0]);
        if (!doesOverlapWithBooking(inputArray[0], inputArray[2], inputArray[3])) {
            return false;
        }
        UserRowDTO deleteUserBookingDTO = new UserRowDTO(userAuthManager.getCurrentUser(), inputArray[1],
                bookingDurationToDelete[0], bookingDurationToDelete[1]);
        UserRowDTO insertUserAvailabilityDTO = new UserRowDTO(userAuthManager.getCurrentUser(),
                bookingDurationToDelete[0], bookingDurationToDelete[1]);
        couldDeleteBooking = bookingManagerImpl.deleteUserRow(deleteUserBookingDTO);
        availabilityManagerImpl.insertUserRow(insertUserAvailabilityDTO);

        return couldDeleteBooking;
    }

    /**
     * Converts startTime and endTime to a Timestamp array. This is done to simplify the booking interval validation
     * process.
     *
     * @param startTime is a String in the form of (hh:mm)
     * @param endTime is a String in the form of (hh:mm)
     * @param date is the day the duration is taking place
     * @return a Timestamp array representing a booking's start and end times
     */
    @NotNull
    public Timestamp[] formatDuration(String startTime, String endTime, String date) {
        String startDateString = getParsableDateTimeString(date, startTime);
        String endDateString = getParsableDateTimeString(date, endTime);
        DateFormat sdf = new SimpleDateFormat(Constants.MONTH_TIME_FORMAT);
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;
        try {
            Date parsedStartTime = sdf.parse(startDateString);
            Date parsedEndTime = sdf.parse(endDateString);
            startTimestamp = new Timestamp(parsedStartTime.getTime());
            endTimestamp = new Timestamp(parsedEndTime.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp[] result = new Timestamp[2];
        result[0] = startTimestamp;
        result[1] = endTimestamp;

        return result;
    }

    /**
     * Creates a UserRowDTO to model the input as received from the user. doesOverlapWithBooking() then uses
     * bookingManagerImpl to determine whether an existing booking overlaps with the duration that the user is
     * attempting to add.
     *
     * @param date used to check whether a current booking exists
     * @param startTimeInput of a particular booking duration
     * @param endTimeInput of a particular booking duration
     * @return true if a booking exists for the given date and duration, false otherwise
     */
    public boolean doesOverlapWithBooking(String date, String startTimeInput, String endTimeInput) {
        Timestamp[] duration = formatDuration(startTimeInput, endTimeInput, date);
        UserRowDTO checkBookingDTO = new UserRowDTO(userAuthManager.getCurrentUser(), duration[0], duration[1]);
        BookingManagerImpl bookingManagerCast = (BookingManagerImpl) bookingManagerImpl;

        return bookingManagerCast.doesBookingExistDuringThisDuration(checkBookingDTO);
    }

    /**
     * Creates a UserRowDTO to model the input as received from the user. doesOverlapWithOpening() then uses
     * availabilityManagerImpl to determine whether an existing availability overlaps with the duration that the user
     * is attempting to add.
     *
     * @param date used to check whether a current availability exists
     * @param availabilityStartTimeInput of a particular availability duration
     * @param availabilityEndTimeInput of a particular availability duration
     * @return true if an availability exists for the given date and duration, false otherwise
     */
    public boolean doesOverlapWithOpening(String date, String availabilityStartTimeInput,
                                          String availabilityEndTimeInput) {
        Timestamp[] duration = formatDuration(availabilityStartTimeInput, availabilityEndTimeInput, date);
        UserRowDTO checkBookingDTO = new UserRowDTO(userAuthManager.getCurrentUser(), duration[0], duration[1]);
        AvailabilityManagerImpl availabilityManagerCast = (AvailabilityManagerImpl) availabilityManagerImpl;

        return availabilityManagerCast.doesAvailabilityAlreadyExist(checkBookingDTO);
    }

    /**
     * Returns the date and time in the following format: "mm/dd/2020 hh:mm".
     * As of now, getParsableDateTimeString()'s implementation assumes all bookings occur in 2020 in to minimize the
     * input requested from the user.
     *
     * @param date as provided by the user
     * @param time as provided by the user
     */
    public String getParsableDateTimeString(String date, String time) {
        return date + Constants.YEAR_2020 + Constants.SPACE + time;
    }

    /**
     * Creates a UserRowDTO containing the availability interval to be removed and then uses availabilityManagerImpl to
     * perform the deletion.
     *
     * @param availabilityToBeRemoved availability interval to be removed
     */
    public void deleteUserAvailabilityToAddBookingInterval(@NotNull Timestamp[] availabilityToBeRemoved) {
        UserRowDTO deleteUserOpeningDTO = new UserRowDTO(userAuthManager.getCurrentUser(),
                availabilityToBeRemoved[0], availabilityToBeRemoved[1]);
        availabilityManagerImpl.deleteUserRow(deleteUserOpeningDTO);
    }

    /**
     * Creates a UserRowDTO containing the companyName and booking duration to be added. insertBookingInterval() then
     * uses bookingManagerImpl to perform the insertion into the backend.
     *
     * @param companyName is the name of the company the user is booking time with
     * @param bookingDuration booking interval as received from the user
     * @return true if the parameter booking was inserted into the backend successfully, false otherwise
     */
    public boolean insertBookingInterval(String companyName, @NotNull Timestamp[] bookingDuration) {
        boolean didBookingInsertSucceed;
        UserRowDTO insertUserBookingDTO = new UserRowDTO(userAuthManager.getCurrentUser(), companyName,
                bookingDuration[0], bookingDuration[1]);
        didBookingInsertSucceed = bookingManagerImpl.insertUserRow(insertUserBookingDTO);

        return didBookingInsertSucceed;
    }

    /**
     * Attempts to add a booking by traversing the list of open availability and splitting a time block if necessary.
     * For example, let availability = [12:00, 14:00] and let booking = [12:30, 13:00]. Then we split the original
     * availability time block into two parts as follows: [12:00, 12:30] and [13:00, 14:00].
     *
     * The current availability now reflects the booking being added from [12:30, 13:00].
     *
     * @param currentOpeningsRows represents all rows of availability for a given user
     * @param bookingDuration Timestamp array representing [startTime, endTime] of the desired booking
     * @return a reference to the availability interval to be removed, if it's possible to add the booking,
     * null otherwise
     */
    public Timestamp[] traverseCurrentOpeningsAndSplitTimeBlockIfNecessary(
            @NotNull Set<UserRowDTO> currentOpeningsRows,
            @NotNull Timestamp[] bookingDuration) {
        Timestamp[] intersectionResult, availabilityDuration;
        Timestamp[] availabilityToBeRemoved = null;

        for (UserRowDTO currentOpeningRow : currentOpeningsRows) {
            Timestamp retrievedAvailabilityStartTime = currentOpeningRow.getStartTime();
            Timestamp retrievedAvailabilityEndTime = currentOpeningRow.getEndTime();
            availabilityDuration = new Timestamp[]{retrievedAvailabilityStartTime, retrievedAvailabilityEndTime};
            intersectionResult = ManagerDAO.getIntervalIntersection(availabilityDuration, bookingDuration);
            if (intersectionResult == null) {
                continue;
            }
            if (intersectionResult[0].compareTo(bookingDuration[0]) == 0 &&
                    intersectionResult[1].compareTo(bookingDuration[1]) == 0) {
                Timestamp updatedAvailabilityStartDuration1 = availabilityDuration[0];
                Timestamp updatedAvailabilityEndDuration1 = bookingDuration[0];
                Timestamp updatedAvailabilityStartDuration2 = bookingDuration[1];
                Timestamp updatedAvailabilityEndDuration2 = availabilityDuration[1];
                if (updatedAvailabilityStartDuration1.compareTo(updatedAvailabilityEndDuration1) != 0) {
                    UserRowDTO insertAvailabilityDTO = new UserRowDTO(userAuthManager.getCurrentUser(),
                            updatedAvailabilityStartDuration1, updatedAvailabilityEndDuration1);
                    availabilityManagerImpl.insertUserRow(insertAvailabilityDTO);
                }
                if (updatedAvailabilityStartDuration2.compareTo(updatedAvailabilityEndDuration2) != 0) {
                    UserRowDTO insertAvailabilityDTO = new UserRowDTO(userAuthManager.getCurrentUser(),
                            updatedAvailabilityStartDuration2, updatedAvailabilityEndDuration2);
                    availabilityManagerImpl.insertUserRow(insertAvailabilityDTO);
                }
                availabilityToBeRemoved = availabilityDuration;
                break;
            }
        }

        return availabilityToBeRemoved;
    }
}
