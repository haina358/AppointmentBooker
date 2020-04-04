package com.company.managerDAO;

import com.company.mySQLDelegates.MySQLConnectionDelegate;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * An implementation of the ManagerDAO interface that is responsible for executing SQL queries related to the user
 * bookings table in the MySQL backend.
 */
public class BookingManagerImpl implements ManagerDAO {

    // Database delegate used to connect to and disconnect from the MySQL backend.
    @NotNull private final MySQLConnectionDelegate connectionDelegate;

    public BookingManagerImpl(@NotNull MySQLConnectionDelegate connectionDelegate) {
        this.connectionDelegate = connectionDelegate;
    }

    /**
     * Fetches and returns a set of all bookings from the backend for a given user. Bookings are ordered in descending
     * order by date.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user bookings MySQL table
     * @return a set of userRowDTOs representing all current openings for a particular user, null if no openings exist
     */
    @Override
    public Set<UserRowDTO> getAllUserRows(@NotNull UserRowDTO userRowDTO) {
        // LinkedHashSet is used to preserve the ordering of the booking dates as retrieved by the query.
        Set<UserRowDTO> userRowDTOSet = new LinkedHashSet<>();
        String getAllCurrentBookingsQuery = Constants.QUERY_GET_ALL_CURRENT_BOOKINGS;
        try (PreparedStatement statement = connectionDelegate.connect().prepareStatement(getAllCurrentBookingsQuery)) {
            statement.setString(1, userRowDTO.getUserName());
            ResultSet rs = statement.executeQuery();
            if (!rs.isBeforeFirst()) {
                return null;
            }
            while (rs.next()) {
                UserRowDTO extractedUserRowDTO = extractUserRowFromResultSet(rs);
                userRowDTOSet.add(extractedUserRowDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionDelegate.disconnect();
        }

        return userRowDTOSet;
    }

    /**
     * Inserts a booking into the bookings table for a particular user and returns true if successful.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user bookings MySQL table
     * @return true if the booking was inserted successfully, false otherwise
     */
    @Override
    public boolean insertUserRow(@NotNull UserRowDTO userRowDTO) {
        boolean couldInsertBooking;
        String insertIntoBookingsQuery = Constants.QUERY_INSERT_INTO_BOOKINGS;
        try (PreparedStatement statement = connectionDelegate.connect().prepareStatement(insertIntoBookingsQuery)) {
            statement.setString(1, userRowDTO.getUserName());
            statement.setString(2, userRowDTO.getCompanyName());
            statement.setTimestamp(3, userRowDTO.getStartTime(), Constants.CALENDAR);
            statement.setTimestamp(4, userRowDTO.getEndTime(), Constants.CALENDAR);
            statement.executeUpdate();
            statement.close();
            couldInsertBooking = true;
        } catch (SQLException e) {
            couldInsertBooking = false;
        } finally {
            connectionDelegate.disconnect();
        }

        return couldInsertBooking;
    }

    /**
     * Deletes a booking from the bookings table for a particular user and returns true if successful.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user bookings MySQL table
     * @return true if the booking was deleted successfully, false otherwise
     */
    @Override
    public boolean deleteUserRow(@NotNull UserRowDTO userRowDTO) {
        boolean couldDeleteBooking;
        String deleteFromBookingsQuery = Constants.QUERY_DELETE_FROM_BOOKINGS;
        try (PreparedStatement statement = connectionDelegate.connect().prepareStatement(deleteFromBookingsQuery)) {
            statement.setString(1, userRowDTO.getUserName());
            statement.setString(2, userRowDTO.getCompanyName());
            statement.setTimestamp(3, userRowDTO.getStartTime(), Constants.CALENDAR);
            statement.setTimestamp(4, userRowDTO.getEndTime(), Constants.CALENDAR);
            statement.executeUpdate();
            statement.close();
            couldDeleteBooking = true;
        } catch (SQLException e) {
            couldDeleteBooking = false;
        } finally {
            connectionDelegate.disconnect();
        }

        return couldDeleteBooking;
    }

    /**
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user bookings MySQL table
     * @return true if a booking exists for the given date and duration, false otherwise
     */
    public boolean doesBookingExistDuringThisDuration(@NotNull UserRowDTO userRowDTO) {
        return doesIntervalAlreadyExist(userRowDTO, Constants.QUERY_SELECT_FROM_BOOKINGS);
    }

    /**
     * Given a ResultSet Object as obtained from a SELECT query, extractUserRowFromResultSet() extracts the start
     * and end times from the ResultSet and sets those values of a new UserRowDTO before returning it.
     */
    private UserRowDTO extractUserRowFromResultSet(@NotNull ResultSet rs) throws SQLException {
        UserRowDTO userRowDTO = new UserRowDTO();

        userRowDTO.setCompanyName(rs.getString(1));
        userRowDTO.setStartTime(rs.getTimestamp(2, Constants.CALENDAR));
        userRowDTO.setEndTime(rs.getTimestamp(3, Constants.CALENDAR));

        return userRowDTO;
    }
}
