package com.github.pavlidise.acmebooking.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(final String roomName) {
        super("Room with name '" + roomName + "' not found");
    }
}
