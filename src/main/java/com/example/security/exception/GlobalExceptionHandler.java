package com.example.security.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        return createErrorResponse(ex, ErrorCode.DUPLICATE_USERNAME);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return createErrorResponse(ex, ErrorCode.DUPLICATE_EMAIL);
    }

    @ExceptionHandler(DuplicatePhoneNumberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicatePhoneNumberException(DuplicatePhoneNumberException ex) {
        return createErrorResponse(ex, ErrorCode.DUPLICATE_PHONE_NUMBER);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return createErrorResponse(ex, ErrorCode.USER_NOT_FOUND);
    }


    private ResponseEntity<ErrorResponse> createErrorResponse(RuntimeException ex,
                                                              ErrorCode errorCode) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
                errorCode.getStatus().value(),
                ex.getMessage(), errorCode.getLabel());

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
