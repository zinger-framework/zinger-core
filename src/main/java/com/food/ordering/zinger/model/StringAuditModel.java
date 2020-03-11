package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.food.ordering.zinger.enums.AuditEnum;

import java.sql.Date;

public class StringAuditModel {
    private String id;
    private String mobile;
    private AuditEnum message;
    private String updatedValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public AuditEnum getMessage() {
        return message;
    }

    public void setMessage(AuditEnum message) {
        this.message = message;
    }

    public String getUpdatedValue() {
        return updatedValue;
    }

    public void setUpdatedValue(String updatedValue) {
        this.updatedValue = updatedValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StringAuditModel{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", message=" + message +
                ", updatedValue='" + updatedValue + '\'' +
                ", date=" + date +
                '}';
    }
}
