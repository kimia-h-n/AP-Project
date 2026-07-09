package com.example.sales.exception;

public class AdViewNotAllowedException extends BaseException {
    public AdViewNotAllowedException() {
        super("Ad view isn't allowed", ErrorCode.AD_VIEW_NOT_ALLOWED);

    }
}
