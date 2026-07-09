package com.example.sales.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND),

    DUPLICATE_EMAIL("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_NUMBER("PHONE_NUMBER_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_USERNAME("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT),

    AD_NOT_FOUND("AD_NOT_FOUND", HttpStatus.NOT_FOUND),
    AD_ALREADY_FAVORITE("AD_ALREADY_FAVORITE", HttpStatus.CONFLICT),
    AD_NOT_FAVORITE("AD_NOT_FAVORITE", HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    AD_VIEW_NOT_ALLOWED("NOT_ALLOWED_AD_VIEW", HttpStatus.BAD_REQUEST);

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
