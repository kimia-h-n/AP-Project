package com.example.sales.exception;

public class AdNotFoundException extends BaseException {
    public AdNotFoundException() {
        super("Ad is not found!", ErrorCode.AD_NOT_FOUND);
    }
}
