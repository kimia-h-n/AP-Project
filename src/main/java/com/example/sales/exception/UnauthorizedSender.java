package com.example.sales.exception;

public class UnauthorizedSender extends BaseException {

    public UnauthorizedSender() {
        super("Unauthorized message sender", ErrorCode.UNAUTHORIZED_SENDER);
    }

}
