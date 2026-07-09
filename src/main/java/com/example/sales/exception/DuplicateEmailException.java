package com.example.sales.exception;

public class DuplicateEmailException extends BaseException {
    public DuplicateEmailException() {
        super("Email already exists", ErrorCode.DUPLICATE_EMAIL);
    }
}
