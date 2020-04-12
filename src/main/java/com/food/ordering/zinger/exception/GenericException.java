package com.food.ordering.zinger.exception;

import com.food.ordering.zinger.model.Response;

public class GenericException extends Exception {
    private Integer code;
    private String message;

    public GenericException(Response response) {
        this.setCode(response.getCode()) ;
        this.setMessage(response.getMessage()) ;
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

