package com.company.userAuthDelegates;

import com.company.mySQLDelegates.MySQLConnectionDelegate;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Facilitates login between users and the backend MySQL database.
 */
public class LoginUserDelegate {

    // Database delegate used to connect to and disconnect from the MySQL backend.
    @NotNull private final MySQLConnectionDelegate connectionDelegate;

    public LoginUserDelegate(@NotNull MySQLConnectionDelegate connectionDelegate) {
        this.connectionDelegate = connectionDelegate;
    }

    /**
     * Utilizes BCrypt's checkpw() method to verify that the computed hash of passwordPlaintext matches the one stored
     * for username.
     *
     * @param username as received from UserAuthDelegate
     * @param passwordPlaintext as as received from UserAuthDelegate
     * @return true if the login was successful, false otherwise
     */
    public boolean login(String username, String passwordPlaintext) {
        String storedHash = getGetStoredHash(username);

        return (storedHash != null) && BCrypt.checkpw(passwordPlaintext, storedHash);
    }

    /**
     * Executes a SQL query to fetch the stored password hash for username.
     *
     * @param username as received from UserAuthDelegate
     * @return the stored password hash for the parameter user, null otherwise
     */
    private String getGetStoredHash(String username) {
        String storedHash = null;
        try {
            PreparedStatement getStoredHashStmt = connectionDelegate.connect().
                    prepareStatement(Constants.QUERY_GET_STORED_HASH);
            getStoredHashStmt.setString(1, username);
            ResultSet rs = getStoredHashStmt.executeQuery();
            if (rs.next()) {
                storedHash = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionDelegate.disconnect();
        }

        return storedHash;
    }
}
