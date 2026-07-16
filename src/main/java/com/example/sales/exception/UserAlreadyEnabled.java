package com.example.sales.exception;

public class UserAlreadyEnabled extends BaseException {
    public UserAlreadyEnabled() {
        super("User is already enabled!", ErrorCode.USER_ALREADY_ENABLED);
    }
}
