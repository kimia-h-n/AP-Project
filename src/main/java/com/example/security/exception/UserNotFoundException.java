package com.example.security.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User is not found!");
    }
}
