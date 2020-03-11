package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.food.ordering.zinger.enums.Priority;

import java.sql.Date;

public class AuditModel {
    private Integer id;
    private String sid;
    private String mobile;
    private Integer errCode;
    private String message;
    private String updatedValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;

    private Priority priority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "AuditModel{" +
                "id=" + id +
                ", sid='" + sid + '\'' +
                ", mobile='" + mobile + '\'' +
                ", errCode=" + errCode +
                ", message='" + message + '\'' +
                ", updatedValue='" + updatedValue + '\'' +
                ", date=" + date +
                ", priority=" + priority +
                '}';
    }
}
