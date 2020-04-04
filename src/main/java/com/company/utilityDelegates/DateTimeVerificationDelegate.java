package com.company.utilityDelegates;

import com.company.utilityDelegates.constants.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for purposes of verifying date and time inputs using the DateFormat class as well as Regex.
 */
public class DateTimeVerificationDelegate {

    private final DateFormat sdfTime;
    private final DateFormat sdfDate;

    public DateTimeVerificationDelegate() {
        // Instantiating instances of SimpleDateFormat with the specified formats.
        sdfTime = new SimpleDateFormat(Constants.TIME_FORMAT);
        sdfDate = new SimpleDateFormat(Constants.DATE_FORMAT);

        // Disabling lenient interpretation enforces strict date validation. Dates such as "27/43" will cause an
        // exception when parsed.
        sdfDate.setLenient(false);
        sdfTime.setLenient(false);
    }

    /**
     * Attempts to parse timeInput using sdfTime. Returns true if timeInput conforms to the hh:mm format.
     *
     * @param timeInput is expected to be a String of the format hh:mm
     */
    public boolean isValidTime(String timeInput) {
        try {
            sdfTime.parse(timeInput);
            return timeInput.matches(Constants.TIME_REGEX);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Attempts to parse dateInput using sdfDate. Returns true if dateInput conforms to the MM/dd format.
     * Disabling the leniency of sdfDate in the constructor causes sdfDate to treat Feb. 29th invalid input. Therefore,
     * a check is made specifically for "02/29".
     *
     * @param dateInput is expected to be a String of the format MM/dd
     */
    public boolean isValidDate(String dateInput) {
        try {
            sdfDate.parse(dateInput);
            return dateInput.matches(Constants.DATE_REGEX);
        } catch (Exception e) {
            return dateInput.equals(Constants.LEAP_DAY);
        }
    }

    /**
     * Ensures that the provided start time is before the end time and that both times conform to the hh:mm format.
     *
     * @param startTime of a duration in the format hh:mm
     * @param endTime of a duration in the format hh:mm
     * @return whether the start time is before the end time
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidTimeInterval(String startTime, String endTime) {
        try {
            Date intervalStartTime = sdfTime.parse(startTime);
            Date intervalEndTime = sdfTime.parse(endTime);
            boolean isStartTimeValid = isValidTime(startTime);
            boolean isEndTimeValid = isValidTime(endTime);

            return intervalStartTime.before(intervalEndTime) && isStartTimeValid && isEndTimeValid;
        } catch (Exception e) {
            return false;
        }
    }
}
