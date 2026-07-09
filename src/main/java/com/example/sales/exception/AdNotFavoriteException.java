package com.example.sales.exception;

public class AdNotFavoriteException extends BaseException {
    public AdNotFavoriteException() {
        super("Ad not favorite", ErrorCode.AD_NOT_FAVORITE);
    }
}
