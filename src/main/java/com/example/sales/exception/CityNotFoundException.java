package com.example.sales.exception;

public class CityNotFoundException extends BaseException {
    public CityNotFoundException() {
        super("City wasn't found by id", ErrorCode.CITY_NOT_FOUND);
    }
}
