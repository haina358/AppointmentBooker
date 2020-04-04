package com.company.managerDAO;

import com.company.mySQLDelegates.MySQLConnectionDelegate;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An implementation of the ManagerDAO interface that is responsible for executing SQL queries related to the
 * user availability/openings table in the MySQL backend.
 */
public class AvailabilityManagerImpl implements ManagerDAO {

    // Database delegate used to connect to and disconnect from the MySQL backend.
    @NotNull private final MySQLConnectionDelegate connectionDelegate;

    public AvailabilityManagerImpl(@NotNull MySQLConnectionDelegate connectionDelegate) {
        this.connectionDelegate = connectionDelegate;
    }

    /**
     * Fetches and returns a set of all availability from the backend for a given user. User availability is ordered in
     * descending order by date.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user availability MySQL table
     * @return a set of userRowDTOs representing all current openings for a particular user, null if no openings exist
     */
    @Override
    public Set<UserRowDTO> getAllUserRows(@NotNull UserRowDTO userRowDTO) {
        // LinkedHashSet is used to preserve the ordering of the availability dates as retrieved by the query.
        Set<UserRowDTO> userRowDTOSet = new LinkedHashSet<>();
        String getAllCurrentOpeningsQuery = Constants.QUERY_GET_ALL_CURRENT_OPENINGS;
        try (PreparedStatement statement = connectionDelegate.connect().prepareStatement(getAllCurrentOpeningsQuery)) {
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
     * Inserts an availability into the availability table for a particular user and returns true if successful.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user availability MySQL table
     * @return true if the availability was inserted successfully, false otherwise
     */
    @Override
    public boolean insertUserRow(@NotNull UserRowDTO userRowDTO) {
        boolean couldInsertOpening;
        String insertOpeningQuery = Constants.QUERY_INSERT_INTO_OPENINGS;
        try (PreparedStatement st = connectionDelegate.connect().prepareStatement(insertOpeningQuery)) {
            st.setString(1, userRowDTO.getUserName());
            st.setTimestamp(2, userRowDTO.getStartTime(), Constants.CALENDAR);
            st.setTimestamp(3, userRowDTO.getEndTime(), Constants.CALENDAR);
            st.executeUpdate();
            couldInsertOpening = true;
        } catch (SQLException e) {
            couldInsertOpening = false;
        } finally {
            connectionDelegate.disconnect();
        }

        return couldInsertOpening;
    }

    /**
     * Deletes an availability from the availability table for a particular user and returns true if successful.
     *
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user availability MySQL table
     * @return true if the availability was deleted successfully, false otherwise
     */
    @Override
    public boolean deleteUserRow(@NotNull UserRowDTO userRowDTO) {
        boolean couldDeleteOpening;
        String deleteFromOpeningsQuery = Constants.QUERY_DELETE_FROM_OPENINGS;
        try (PreparedStatement statement = connectionDelegate.connect().prepareStatement(deleteFromOpeningsQuery)) {
            statement.setString(1, userRowDTO.getUserName());
            statement.setTimestamp(2, userRowDTO.getStartTime(), Constants.CALENDAR);
            statement.setTimestamp(3, userRowDTO.getEndTime(), Constants.CALENDAR);
            statement.executeUpdate();
            couldDeleteOpening = true;
        } catch (SQLException e) {
            couldDeleteOpening = false;
        } finally {
            connectionDelegate.disconnect();
        }

        return couldDeleteOpening;
    }

    /**
     * @param userRowDTO Data Transfer Object (DTO) representing a single row in the user availability MySQL table
     * @return true if an availability exists for the given date and duration, false otherwise
     */
    public boolean doesAvailabilityAlreadyExist(@NotNull UserRowDTO userRowDTO) {
        return doesIntervalAlreadyExist(userRowDTO, Constants.QUERY_SELECT_FROM_OPENINGS);
    }

    /**
     * Given a ResultSet Object as obtained from a SELECT query, extractUserRowFromResultSet() extracts the start
     * and end times from the ResultSet and sets those values of a new UserRowDTO before returning it.
     */
    private UserRowDTO extractUserRowFromResultSet(@NotNull ResultSet rs) throws SQLException {
        UserRowDTO userRowDTO = new UserRowDTO();

        userRowDTO.setStartTime(rs.getTimestamp(1, Constants.CALENDAR));
        userRowDTO.setEndTime(rs.getTimestamp(2, Constants.CALENDAR));

        return userRowDTO;
    }
}
