package com.example.security.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND("USERNAME_NOT_FOUND", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_NUMBER("PHONE_NUMBER_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_USERNAME("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT);

    private final String label;
    private final HttpStatus status;

    ErrorCode(String label, HttpStatus status) {
        this.label = label;
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
