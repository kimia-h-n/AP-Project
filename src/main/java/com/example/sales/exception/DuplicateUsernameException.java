package com.example.sales.exception;

public class DuplicateUsernameException extends BaseException {
    public DuplicateUsernameException() {
        super("Username already exists.", ErrorCode.DUPLICATE_USERNAME);
    }
}
