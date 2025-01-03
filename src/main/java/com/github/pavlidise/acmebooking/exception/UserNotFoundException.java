package com.github.pavlidise.acmebooking.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String errorMsg) {
        super(errorMsg);
    }
}
