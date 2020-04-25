package com.food.ordering.zinger.model;

import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.ErrorLog;

public class Response<T> {
    private Integer code;
    private String message;
    private Enums.Priority priority;
    private T data;

    public Response() {
        code = ErrorLog.CodeFailure;
        message = ErrorLog.Failure;
        priority = Enums.Priority.MEDIUM;
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

    public Enums.Priority priorityGet() {
        return priority;
    }

    public void prioritySet(Enums.Priority priority) {
        this.priority = priority;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
