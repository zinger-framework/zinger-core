package com.food.ordering.zinger.exception;

public class InvalidHeaderException extends Exception {
    private Integer code;
    private String message;

    public InvalidHeaderException(Integer code) {
        this(code, null);
    }

    public InvalidHeaderException(Integer code, String message) {
        this.setCode(code) ;
        this.setMessage(message) ;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

