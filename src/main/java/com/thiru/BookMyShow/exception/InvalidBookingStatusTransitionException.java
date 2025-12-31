package com.thiru.BookMyShow.exception;

public class InvalidBookingStatusTransitionException
        extends RuntimeException {

    public InvalidBookingStatusTransitionException(String message) {
        super(message);
    }
}
