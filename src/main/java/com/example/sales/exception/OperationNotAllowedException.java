package com.example.sales.exception;

public class OperationNotAllowedException extends BaseException {
    public OperationNotAllowedException() {
        super("Operation not allowed", ErrorCode.OPERATION_NOT_ALLOWED);
    }
}
