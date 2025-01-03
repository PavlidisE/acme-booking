package com.github.pavlidise.acmebooking.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(final String message) {
        super(message);
    }
}
