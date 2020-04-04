package com.company.userAuthDelegates;

import com.company.exceptions.MaxUsernameLengthViolationException;
import com.company.mySQLDelegates.MySQLConnectionDelegate;
import org.jetbrains.annotations.NotNull;

/**
 * Authentication implementation responsible for transferring control flow to either login or registration.
 * UserAuthImpl also facilitates retrieval of the currently logged in user by returning their username as a String.
 */
public class UserAuthManager {

    // String referencing the username of the currently logged in user, null if no user is logged in. Used to simulate
    // the current user session.
    private String currentUser;
    // Facilitates login between users and the backend MySQL database.
    @NotNull private final LoginUserDelegate loginUserDelegate;
    // Facilitates registration between users and the backend MySQL database.
    @NotNull private final RegisterUserDelegate registerUserDelegate;

    public UserAuthManager(@NotNull MySQLConnectionDelegate connectionDelegate) {
        // Database delegate used to connect to and disconnect from the MySQL backend.
        loginUserDelegate = new LoginUserDelegate(connectionDelegate);
        registerUserDelegate = new RegisterUserDelegate(connectionDelegate);
    }

    /**
     * Delegates registration logic to RegisterUserDelegate and returns true if the operation was successful, false
     * otherwise.
     *
     * @throws MaxUsernameLengthViolationException if username exceeds 100 characters
     *
     * @param username as provided by the user
     * @param passwordPlaintext as provided by the user
     * @return true if the registration was successful, false otherwise
     */
    public boolean register(String username, String passwordPlaintext) throws MaxUsernameLengthViolationException {
        return registerUserDelegate.register(username, passwordPlaintext);
    }

    /**
     * Delegates login logic to LoginUserDelegate and returns true if the operation was successful, false otherwise.
     * Sets currentUser to username if the login was successful.
     *
     * @param username as provided by the user
     * @param passwordPlaintext as provided by the user
     * @return true if the login was successful, false otherwise
     */
    public boolean login(String username, String passwordPlaintext) {
        boolean isLoginSuccessful = loginUserDelegate.login(username, passwordPlaintext);
        if (isLoginSuccessful) {
            currentUser = username;
        }

        return isLoginSuccessful;
    }

    /**
     * Sets currentUser to null, effectively logging the user out.
     */
    public void logOut() {
        currentUser = null;
    }

    /**
     * Returns the username of the currently logged in user, null otherwise.
     */
    public String getCurrentUser() {
        return currentUser;
    }
}
