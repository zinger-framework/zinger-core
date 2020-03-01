package com.food.ordering.ssn.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class TransactionModel {
    private String transactionId;
    private String bankTransactionId;
    private String currency;
    private String responseCode;
    private String responseMessage;
    private String gatewayName;
    private String bankName;
    private String paymentMode;
    private String checksumHash;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern= "dd/MM/yyyy HH:mm:ss")
    private Date date;

    public TransactionModel() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public void setChecksumHash(String checksumHash) {
        this.checksumHash = checksumHash;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "transactionId='" + transactionId + '\'' +
                ", bankTransactionId='" + bankTransactionId + '\'' +
                ", currency='" + currency + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                ", gatewayName='" + gatewayName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", checksumHash='" + checksumHash + '\'' +
                ", date=" + date +
                '}';
    }
}
