package com.food.ordering.zinger.interceptor;

public class InvalidException extends RuntimeException {
    private String message;

    public InvalidException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
