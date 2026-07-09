package com.example.sales.exception;

public class DuplicatePhoneNumberException extends BaseException {
    public DuplicatePhoneNumberException() {
        super("Phone number already exists.", ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}
