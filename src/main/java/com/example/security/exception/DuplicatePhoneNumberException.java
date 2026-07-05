package com.example.security.exception;

public class DuplicatePhoneNumberException extends RuntimeException {
    public DuplicatePhoneNumberException() {
        super("Phone number already exists.");
    }
}
