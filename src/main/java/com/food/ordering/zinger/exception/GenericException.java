package com.food.ordering.zinger.exception;

import com.food.ordering.zinger.model.Response;

public class GenericException extends RuntimeException {
    private Response response;

    public GenericException(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "GenericException{" +
                "response=" + response +
                '}';
    }
}
