package com.food.ordering.zinger.model.logger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.food.ordering.zinger.constant.Enums;

import java.sql.Timestamp;

public class ApplicationLogModel {

    private Enums.HttpRequestType requestType;
    private String endpointUrl;
    private String requestHeader;
    private String requestObject;
    private String responseObject;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp date;



    public ApplicationLogModel(Enums.HttpRequestType requestType, String endpointUrl, String requestHeader, String requestObject, String responseObject) {
        this.requestType = requestType;
        this.endpointUrl = endpointUrl;
        this.requestHeader = requestHeader;
        this.requestObject = requestObject;
        this.responseObject = responseObject;
    }

    public Enums.HttpRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(Enums.HttpRequestType requestType) {
        this.requestType = requestType;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(String requestObject) {
        this.requestObject = requestObject;
    }

    public String getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(String responseObject) {
        this.responseObject = responseObject;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ActivityLogModel{" +
                "requestType=" + requestType +
                ", endpointUrl='" + endpointUrl + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", requestObject='" + requestObject + '\'' +
                ", responseObject='" + responseObject + '\'' +
                ", date=" + date +
                '}';
    }
}


