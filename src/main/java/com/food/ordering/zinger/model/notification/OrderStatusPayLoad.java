package com.food.ordering.zinger.model.notification;

import com.food.ordering.zinger.constant.Enums;

public class OrderStatusPayLoad {

    Integer orderId;
    Enums.OrderStatus orderStatus;
    String shopName;
    String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Enums.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Enums.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
