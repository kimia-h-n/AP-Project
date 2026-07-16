package com.example.sales.exception;

public class AlreadyBlockedException extends BaseException {

    public AlreadyBlockedException() {
        super("User is already blocked!", ErrorCode.USER_ALREADY_BLOCKED);
    }
}
