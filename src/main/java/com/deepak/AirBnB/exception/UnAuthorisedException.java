package com.deepak.AirBnB.exception;

public class UnAuthorisedException extends RuntimeException {
    public UnAuthorisedException(String message) {
        super(message);
    }
}
