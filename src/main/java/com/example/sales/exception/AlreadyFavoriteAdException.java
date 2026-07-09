package com.example.sales.exception;

public class AlreadyFavoriteAdException extends BaseException {
    public AlreadyFavoriteAdException() {
        super("Ad is already favorite", ErrorCode.AD_ALREADY_FAVORITE);
    }
}
