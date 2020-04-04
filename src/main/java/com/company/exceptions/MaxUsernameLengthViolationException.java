package com.company.exceptions;

import com.company.utilityDelegates.constants.Constants;

/**
 * Exception to be thrown when a username exceeds 100 characters in the registration process.
 */
public class MaxUsernameLengthViolationException extends Exception {

    private final String exceptionMessage;

    public MaxUsernameLengthViolationException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return exceptionMessage + Constants.NEW_LINE;
    }
}
