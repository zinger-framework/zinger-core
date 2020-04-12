package com.food.ordering.zinger.exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(final String message) {
        super(message);
    }

    public DataNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

