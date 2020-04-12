package com.food.ordering.zinger.exception;

public class ConstraintException extends RuntimeException {
    public ConstraintException(final String message) {
        super(message);
    }

    public ConstraintException(final String message, final Throwable cause) {
        super(message, cause);
    }
}


