package com.food.ordering.zinger.model.notification;

import com.food.ordering.zinger.constant.Enums;

import java.util.ArrayList;

public class SellerPayLoad {
    String userName;
    Integer orderId;
    Double amount;
    ArrayList<String> itemList;
    String orderType;
    Enums.OrderStatus orderStatus;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ArrayList<String> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Enums.OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Enums.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "SellerPayLoad{" +
                "userName='" + userName + '\'' +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", itemList=" + itemList +
                ", orderType='" + orderType + '\'' +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
