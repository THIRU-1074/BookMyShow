package com.thiru.BookMyShow.exception;

public class InsufficientSeatsException extends RuntimeException {

    public InsufficientSeatsException(Long requested, Long available) {
        super("Requested seats: " + requested +
                ", but only " + available + " seats are available.");
    }
}
