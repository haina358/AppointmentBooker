package com.company.userAuthDelegates;

import com.company.exceptions.MaxUsernameLengthViolationException;
import com.company.mySQLDelegates.MySQLConnectionDelegate;
import com.company.utilityDelegates.constants.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Facilitates registration between users and the backend MySQL database.
 */
public class RegisterUserDelegate {

    // Database delegate used to connect to and disconnect from the MySQL backend.
    @NotNull private final MySQLConnectionDelegate connectionDelegate;

    public RegisterUserDelegate(@NotNull MySQLConnectionDelegate connectionDelegate) {
        this.connectionDelegate = connectionDelegate;
    }

    /**
     * Accesses the database via MySQLConnectionDelegate and attempts to register the user with the provided
     * credentials.
     *
     * @throws MaxUsernameLengthViolationException if username exceeds MAX_USERNAME_LENGTH
     *
     * @param username as received from UserAuthDelegate
     * @param passwordPlaintext as as received from UserAuthDelegate
     * @return true if the registration was successful, false otherwise
     */
    public boolean register(String username, String passwordPlaintext) throws MaxUsernameLengthViolationException {
        if (username.length() > Constants.MAX_USERNAME_LENGTH) {
            throw new MaxUsernameLengthViolationException(Constants.USERNAME_TOO_LONG);
        }
        String passwordHash = hashPassword(passwordPlaintext);
        boolean isRegistrationSuccessful = true;
        try {
            PreparedStatement registerStmt = connectionDelegate.connect().
                    prepareStatement(Constants.QUERY_INSERT_NEW_USER);
            registerStmt.setString(1, username);
            registerStmt.setString(2, passwordHash);
            registerStmt.executeUpdate();
            registerStmt.close();
        } catch (SQLIntegrityConstraintViolationException e) { // Indicates that the username already exists.
            isRegistrationSuccessful = false;
        } catch (SQLException e) {
            e.printStackTrace();
            isRegistrationSuccessful = false;
        } finally {
            connectionDelegate.disconnect();
        }

        return isRegistrationSuccessful;
    }

    /**
     * Using the OpenBSD BCrypt scheme, hashPassword() generates a hash of passwordPlaintext that can be stored in a
     * database. The resulting hash has a length of 60 characters.
     * The workload value is specified above and determines the computational complexity of the hashing.
     *
     * @param passwordPlaintext as provided by the user
     * @return the hashed String value of passwordPlaintext with the 128-bit salt and cost parameter prepended
     */
    private static String hashPassword(String passwordPlaintext) {
        String salt = BCrypt.gensalt(Constants.BCRYPT_WORKLOAD);

        return BCrypt.hashpw(passwordPlaintext, salt);
    }
}

