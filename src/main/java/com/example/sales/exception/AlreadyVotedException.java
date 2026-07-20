package com.example.sales.exception;

public class AlreadyVotedException extends BaseException {
    public AlreadyVotedException() {
        super("User already voted!", ErrorCode.ALREADY_VOTED_ERROR);
    }
}
