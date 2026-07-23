package com.example.sales.exception;

public class BlockedUserLoginException extends BaseException {
    public BlockedUserLoginException() {
        super("Blocked user is trying to login", ErrorCode.USER_BLOCKED_LOGIN_ATTEMPT);
    }
}
