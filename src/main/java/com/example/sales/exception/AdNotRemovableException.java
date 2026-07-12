package com.example.sales.exception;

public class AdNotRemovableException extends BaseException {
    public AdNotRemovableException() {
        super("Ad not removable", ErrorCode.AD_NOT_REMOVABLE);
    }
}
