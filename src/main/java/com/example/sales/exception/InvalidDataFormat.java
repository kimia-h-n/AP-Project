package com.example.sales.exception;

public class InvalidDataFormat extends BaseException {
    public InvalidDataFormat() {
        super("Data format is invalid", ErrorCode.INVALID_DATA_FORMAT);
    }

}
