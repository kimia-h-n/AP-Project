package com.example.sales.exception;

public class SpamNotAllowedException extends BaseException {
    public SpamNotAllowedException() {
        super("Spam is not allowed", ErrorCode.SPAM_NOT_ALLOWED);
    }
}
