package com.example.sales.exception;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super("User is not found!", ErrorCode.USER_NOT_FOUND);
    }
}
