package com.company.managerDAO;

import com.company.mySQLDelegates.MySQLConnectionDelegate;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Interface responsible for providing method prototypes to be used by AvailabilityManagerImpl and BookingManagerImpl.
 * Each UserRowDTO represents a table row within MySQL. Implementation details of getAllUserRows(), insertUserRow(),
 * and deleteUserRow() are determined by the inheriting classes.
 */
public interface ManagerDAO {

    // Database delegate used to connect to and disconnect from the MySQL backend.
    @NotNull MySQLConnectionDelegate connectionDelegate = new MySQLConnectionDelegate();

    Set<UserRowDTO> getAllUserRows(@NotNull UserRowDTO userRowDTO);
    boolean insertUserRow(@NotNull UserRowDTO userRowDTO);
    boolean deleteUserRow(@NotNull UserRowDTO userRowDTO);

    /**
     * Given two time intervals, returns their overlapping interval if possible.
     * The parameter intervals are represented as follows: [startTimeStamp, endTimeStamp].
     *
     * @param interval1 first time interval to be compared
     * @param interval2 second time interval to be compared
     * @return Timestamp[] of size two representing the overlapping interval, or null if no overlapping interval exists
     */
    static Timestamp[] getIntervalIntersection(@NotNull Timestamp[] interval1, @NotNull Timestamp[] interval2) {
        // Attempts to obtain a common start time between the two intervals.
        Timestamp startTimeCheck = (interval1[0].after(interval2[0])) ? interval1[0] : interval2[0];
        // Attempts to obtain a common end time between the two intervals.
        Timestamp endTimeCheck = (interval1[1].before(interval2[1])) ? interval1[1] : interval2[1];
        // True if there exists an intersection or the parameter intervals are equivalent.
        if (startTimeCheck.compareTo(endTimeCheck) <= 0) {
            return new Timestamp[]{startTimeCheck, endTimeCheck};
        } else {
            return null;
        }
    }

    /**
     * Uses the parameter UserRowDTO to determine whether there exists an overlapping interval between the interval
     * stored in the UserRowDTO and any row in the table.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the availability/booking MySQL table
     * @param tableQuery SQL query to be executed that operates on either the bookings or availability table,
     *                   depending on whether this method was called from AvailabilityManagerImpl or BookingManagerImpl
     * @return true if there exists an overlap between the interval represented by userRowDTO and any row in the table
     */
    default boolean doesIntervalAlreadyExist(@NotNull UserRowDTO userRowDTO, String tableQuery) {
        Timestamp[] userInputDuration = new Timestamp[]{userRowDTO.getStartTime(), userRowDTO.getEndTime()};
        try (PreparedStatement statement = connectionDelegate.connect().prepareStatement(tableQuery)) {
            statement.setString(1, userRowDTO.getUserName());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Timestamp retrievedStartTime = rs.getTimestamp(1, Constants.CALENDAR);
                Timestamp retrievedEndTime = rs.getTimestamp(2, Constants.CALENDAR);
                Timestamp[] retrievedAvailabilityDuration = new Timestamp[]{retrievedStartTime, retrievedEndTime};
                // Some or all of the provided availability already exists
                if (getIntervalIntersection(retrievedAvailabilityDuration, userInputDuration) != null) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionDelegate.disconnect();
        }

        return false;
    }
}
