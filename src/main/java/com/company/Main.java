package com.company;

import com.company.managerDAO.AvailabilityManagerImpl;
import com.company.managerDAO.BookingManagerImpl;
import com.company.managerDAO.ManagerDAO;
import com.company.mySQLDelegates.MySQLConnectionDelegate;
import com.company.userAuthDelegates.UserAuthManager;
import com.company.utilityDelegates.DateTimeVerificationDelegate;
import com.company.utilityDelegates.IntervalSchedulerDelegate;
import com.company.utilityDelegates.PrinterDelegate;

import java.util.Scanner;

/**
 * Entry point to the program. Instantiates all necessary objects to begin execution and injects them into the
 * relevant constructors.
 */
public class Main {

    public static void main(String[] args) {
        MySQLConnectionDelegate connectionDelegate = new MySQLConnectionDelegate();
        UserAuthManager userAuthManager = new UserAuthManager(connectionDelegate);
        DateTimeVerificationDelegate dateTimeVerificationDelegate = new DateTimeVerificationDelegate();
        Scanner sc = new Scanner(System.in);
        ManagerDAO bookingManagerImpl = new BookingManagerImpl(connectionDelegate);
        ManagerDAO availabilityManagerImpl = new AvailabilityManagerImpl(connectionDelegate);
        PrinterDelegate printerDelegate = new
                PrinterDelegate(userAuthManager, bookingManagerImpl, availabilityManagerImpl);

        IntervalSchedulerDelegate ISDelegate = new IntervalSchedulerDelegate(userAuthManager, bookingManagerImpl,
                availabilityManagerImpl);
        IntervalScheduler intervalScheduler = new IntervalScheduler(ISDelegate, userAuthManager,
                dateTimeVerificationDelegate, sc, availabilityManagerImpl, printerDelegate);

        intervalScheduler.start();
    }
}
