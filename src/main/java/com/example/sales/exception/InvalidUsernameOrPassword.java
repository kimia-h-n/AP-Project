package com.example.sales.exception;

public class InvalidUsernameOrPassword extends BaseException {

    public InvalidUsernameOrPassword() {
        super("Password or username is invalid", ErrorCode.INVALID_PASSWORD_OR_USERNAME);
    }
}
