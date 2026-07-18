package com.example.sales.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD_OR_USERNAME("INVALID_PASSWORD_OR_USERNAME", HttpStatus.UNAUTHORIZED),
    DUPLICATE_EMAIL("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_NUMBER("PHONE_NUMBER_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_USERNAME("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT),

    UNAUTHORIZED_SENDER("UNAUTHORIZED_SENDER", HttpStatus.UNAUTHORIZED),

    USER_ALREADY_BLOCKED("USER_ALREADY_BLOCKED", HttpStatus.CONFLICT),

    USER_ALREADY_ENABLED("USER_ALREADY_ENABLED", HttpStatus.CONFLICT),

    AD_NOT_FOUND("AD_NOT_FOUND", HttpStatus.NOT_FOUND),

    AD_ALREADY_FAVORITE("AD_ALREADY_FAVORITE", HttpStatus.CONFLICT),

    AD_NOT_FAVORITE("AD_NOT_FAVORITE", HttpStatus.BAD_REQUEST),

    AD_NOT_REMOVABLE("AD_NOT_REMOVABLE", HttpStatus.CONFLICT),

    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", HttpStatus.FORBIDDEN),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    AD_VIEW_NOT_ALLOWED("NOT_ALLOWED_AD_VIEW", HttpStatus.FORBIDDEN);

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
