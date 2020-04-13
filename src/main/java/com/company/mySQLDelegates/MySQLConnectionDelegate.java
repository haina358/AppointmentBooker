package com.company.mySQLDelegates;

import com.company.utilityDelegates.constants.Constants;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * MySQLConnectionDelegate facilitates database connection and disconnection logic. The delegate uses the
 * MYSQL_DATABASE_URL, USERNAME, and PASSWORD constants to establish a database connection and return a Connection
 * object.
 */
public class MySQLConnectionDelegate {

    // Declaration of the database connection object.
    private Connection connection;

    /**
     * Opens a connection to the database referenced by MYSQL_DATABASE_URL to allow the caller to communicate with the
     * backend. Uses the singleton pattern to ensure that a only a single database connection can be established per
     * MySQLConnectionDelegate instance.
     *
     * @return a database Connection object referencing the database in Constants.MYSQL_DATABASE_URL
     */
    public Connection connect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(Constants.MYSQL_DATABASE_URL, Constants.MYSQL_USERNAME,
                        Constants.MYSQL_PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    /**
     * Closes the connection, if open, to the database referenced by MYSQL_DATABASE_URL.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
