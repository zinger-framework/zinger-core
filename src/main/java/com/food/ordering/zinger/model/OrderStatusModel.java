package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.food.ordering.zinger.constant.Enums;

import java.sql.Timestamp;

public class OrderStatusModel {
    private Integer orderId;

    private Enums.OrderStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp updatedTime;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Enums.OrderStatus getOrderStatus() {
        return status;
    }

    public void setOrderStatus(Enums.OrderStatus status) {
        this.status = status;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "OrderStatusModel{" +
                "orderId=" + orderId +
                ", status=" + status +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
