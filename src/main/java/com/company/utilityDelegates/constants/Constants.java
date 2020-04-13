package com.company.utilityDelegates.constants;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Provides access to all constants within the program in one location.
 */
public class Constants {

    // Main menu option constants.
    public static final String MAIN_MENU_NOTE = "Note: Before adding a booking, type <Add> to input your" +
            " availability.\n";
    public static final String MAIN_MENU_DATE = "*\tEnter a date (mm/dd) to start adding a booking.";
    public static final String MAIN_MENU_ADD = "*\t<Add> to input an availability/opening for a given date.";
    public static final String MAIN_MENU_BOOKINGS = "*\t<Bookings> to view current bookings.";
    public static final String MAIN_MENU_OPENINGS = "*\t<Openings> to view current openings.";
    public static final String MAIN_MENU_DELETE = "*\t<Delete> to delete a booking.";
    public static final String MAIN_MENU_CANCEL = "*\t<Cancel> at anytime to return to this menu.";
    public static final String MAIN_MENU_LOGOUT = "*\t<Logout> to log out.";
    public static final String[] MAIN_MENU_OPTIONS = new String[]{MAIN_MENU_NOTE, MAIN_MENU_DATE, MAIN_MENU_ADD,
        MAIN_MENU_BOOKINGS, MAIN_MENU_OPENINGS, MAIN_MENU_DELETE, MAIN_MENU_CANCEL, MAIN_MENU_LOGOUT};

    // Program prompt constants.
    public static final String PROMPT_USERNAME = "Username: ";
    public static final String PROMPT_PASSWORD = "Password: ";
    public static final String PROMPT_CONFIRM_PASSWORD = "Confirm password: ";
    public static final String PROMPT_DELETE_DATE = "\nEnter date of booking (mm/dd): ";
    public static final String PROMPT_DELETE_COMPANY = "\nWhich company are you deleting? ";
    public static final String PROMPT_DELETE_START_TIME = "\nEnter the start time of your booking (hh:mm): ";
    public static final String PROMPT_DELETE_END_TIME = "\nEnter the end time of your booking (hh:mm): ";
    public static final String[] DELETE_PROMPTS = new String[]{PROMPT_DELETE_DATE, PROMPT_DELETE_COMPANY,
            PROMPT_DELETE_START_TIME, PROMPT_DELETE_END_TIME};
    public static final String PROMPT_LOGIN_REGISTER = "Type <Login> or <Register> to begin: ";
    public static final String PROMPT_WHICH_COMPANY = "Which company are you booking time with? ";
    public static final String PROMPT_CONFIRM_DELETE_BOOKING = "\nAre you sure you want to delete your booking with" +
            " %s on %s from %s to %s? (<YES>/<NO>) ";
    public static final String PROMPT_BOOKING_START_TIME = "Enter a \033[1mstart\033[0m time (hh:mm) for your" +
            " booking with %s on %s: ";
    public static final String PROMPT_BOOKING_END_TIME = "Enter an \033[1mend\033[0m time (hh:mm) for your" +
            " booking with %s on %s: ";
    public static final String PROMPT_AVAILABILITY_DATE = "\nEnter a date (mm/dd) to begin adding availability or" +
            " type <Cancel> to return to the main menu: ";
    public static final String PROMPT_AVAILABILITY_START_TIME = "Enter a \033[1mstart\033[0m time (hh:mm) for your" +
            " availability on %s: ";
    public static final String PROMPT_AVAILABILITY_END_TIME = "Enter an \033[1mend\033[0m time (hh:mm) for your" +
            " availability on %s: ";
    public static final String PRINTING_BOOKINGS = "\nPrinting all bookings: ";
    public static final String PRINTING_AVAILABILITY = "\nPrinting all availability: ";
    public static final String BOOKING_PRINT_LINE = "%s.) Booking with %s on %s from [%s to %s]";
    public static final String AVAILABILITY_PRINT_LINE = "%s.) Availability on %s from [%s to %s]";

    // User input constants.
    public static final String INPUT_BOOKINGS = "bookings";
    public static final String INPUT_OPENINGS = "openings";
    public static final String INPUT_DELETE = "delete";
    public static final String INPUT_ADD = "add";
    public static final String INPUT_LOGOUT = "logout";
    public static final String INPUT_LOGIN = "login";
    public static final String INPUT_REGISTER = "register";
    public static final String INPUT_CANCEL = "cancel";
    public static final String INPUT_YES = "yes";

    // Program output constants, negative case.
    public static final String INVALID_INPUT = "\n~~~ Invalid input. ~~~\n";
    public static final String INVALID_INPUT_DELETE = "\n~~~ Invalid input. Date must be in (mm/dd) and times must" +
            " be in (hh:mm). (Must also be a valid time interval) ~~~\n";
    public static final String INVALID_LOGIN = "\n~~~ Invalid username or password. ~~~\n";
    public static final String PASSWORD_MISMATCH = "\n~~~ Passwords do not match. ~~~\n";
    public static final String INVALID_START_TIME = "\n~~~ Invalid start time entered. Input must be of the format" +
            " (hh:mm). ~~~\n";
    public static final String INVALID_END_TIME = "\n~~~ Invalid end time entered. Input must be of the format" +
            " (hh:mm). ~~~\n";
    public static final String INVALID_DATE = "\n~~~ Invalid date entered. ~~~\n";
    public static final String ADD_OPENING_FAILURE_1 = "\n~~~ Availability could not be added. Some or all of this" +
            " time block has already been booked. ~~~\n";
    public static final String ADD_OPENING_FAILURE_2 = "\n~~~ Availability could not be added. The entered" +
            " availability block overlaps with an existing one. ~~~\n";
    public static final String USERNAME_TOO_LONG = "\n~~~ Username exceeds 100 characters! ~~~";
    public static final String NO_CURRENT_BOOKINGS = "\n~~~ No current bookings. ~~~\n";
    public static final String NO_CURRENT_AVAILABILITY = "\n~~~ No current availability. ~~~\n";
    public static final String BOOKING_DELETE_FAILURE = "\n~~~ Booking could not be deleted. No booking found with" +
            " %s on %s from [%s to %s]. ~~~\n";
    public static final String REGISTRATION_FAILED = "\n~~~ Registration failed. Username %s already exists. ~~~\n";
    public static final String INVALID_TIME_INTERVAL = "\n~~~ Invalid interval entered: %s to %s. ~~~\n";
    public static final String NO_AVAILABILITY = "\n~~~ You do not have availability during this time. Availability" +
            " could not be added for %s on %s from %s to %s" + ". ~~~\n";
    public static final String NO_AVAILABILITY_ENTERED = "\n~~~ No availability entered for %s. To enter" +
            " availability, type <Add> from the main menu. Redirecting to main menu. ~~~\n";

    // Program output constants, positive case.
    public static final String BOOKING_DELETED = "\nYour booking was successfully deleted!!\n";
    public static final String SUCCESSFUL_REGISTRATION = "\nRegistration successful!\n";
    public static final String BOOKING_ADDED = "\nSuccess!! A booking with %s has been added for %s from %s to %s.\n";
    public static final String SUCCESSFUL_LOGIN = "\nLogin successful! Welcome, %s!\n";
    public static final String AVAILABILITY_ADDED = "\nSuccess!! Availability on %s has been added from %s to %s.\n";

    // Date-Time formatting constants.
    public static final ZoneId ZONE_ID = ZonedDateTime.now(ZoneId.systemDefault()).getZone();
    public static final String TIMEZONE = TimeZone.getTimeZone(ZONE_ID).getID();
    public static final String YEAR_2020 = "/2020";
    public static final String MONTH_TIME_FORMAT = "MM/dd/yyyy HH:mm";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "MM/dd";
    public static final String LEAP_DAY = "02/29";
    public static final String DATE_REGEX = "^\\d{2}\\/\\d{2}$";
    public static final String TIME_REGEX = "^\\d{2}:\\d{2}$";
    public static final String DATE_TIME_PRINT_FORMAT = "MM/dd HH:mm";
    public static final Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIMEZONE));

    // SQL query constants.
    public static final String QUERY_DELETE_FROM_BOOKINGS = "DELETE FROM bookings WHERE user_name = ? AND" +
            " company_name = ? AND start_time = ? AND end_time = ?";
    public static final String QUERY_SELECT_FROM_OPENINGS = "SELECT start_time, end_time FROM openings WHERE" +
            " user_name = ?";
    public static final String QUERY_INSERT_INTO_OPENINGS = "INSERT INTO openings (user_name, start_time, end_time)" +
            " VALUES (?,?,?)";
    public static final String QUERY_DELETE_FROM_OPENINGS = "DELETE FROM openings WHERE user_name = ? AND" +
            " start_time = ? AND end_time = ?";
    public static final String QUERY_INSERT_INTO_BOOKINGS = "INSERT INTO bookings (user_name, company_name," +
            " start_time, end_time) VALUES (?,?,?,?)";
    public static final String QUERY_SELECT_FROM_BOOKINGS = "SELECT start_time, end_time FROM bookings WHERE" +
            " user_name = ?";
    public static final String QUERY_GET_STORED_HASH = "SELECT hashed_pw FROM users WHERE user_name = ?";
    public static final String QUERY_INSERT_NEW_USER = "INSERT INTO users (user_name, hashed_pw) VALUES(?,?)";
    public static final String QUERY_GET_ALL_CURRENT_BOOKINGS = "SELECT company_name, start_time, end_time FROM" +
            " bookings WHERE user_name = ? ORDER BY start_time DESC";
    public static final String QUERY_GET_ALL_CURRENT_OPENINGS = "SELECT start_time, end_time FROM openings WHERE" +
            " user_name = ? ORDER BY start_time DESC";

    // Database configuration constants.
    public static final String MYSQL_DATABASE_URL = "jdbc:mysql://localhost:3306/javabase?serverTimezone=EST";
    public static final String MYSQL_USERNAME = "java";
    public static final String MYSQL_PASSWORD = "password";

    // Miscellaneous constants.
    public static final String SPACE = " ";
    public static final String NEW_LINE = "\n";

    // Registration constants.
    public static final int MAX_USERNAME_LENGTH = 100;
    // Defines the BCrypt workload to use when generating password hashes.
    public static final int BCRYPT_WORKLOAD = 12;
}
