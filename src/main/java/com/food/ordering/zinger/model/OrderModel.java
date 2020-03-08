package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.food.ordering.zinger.enums.OrderStatus;

import java.sql.Date;

public class OrderModel {
    private Integer id;
    private UserModel userModel;
    private TransactionModel transactionModel;
    private ShopModel shopModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;

    private OrderStatus orderStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date lastStatusUpdatedTime;

    private Double price;
    private Double deliveryPrice;
    private String deliveryLocation;
    private String cookingInfo;
    private Double rating;
    private String secretKey;

    public OrderModel() {
        userModel = new UserModel();
        transactionModel = new TransactionModel();
        shopModel = new ShopModel();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public TransactionModel getTransactionModel() {
        return transactionModel;
    }

    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    public ShopModel getShopModel() {
        return shopModel;
    }

    public void setShopModel(ShopModel shopModel) {
        this.shopModel = shopModel;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getLastStatusUpdatedTime() {
        return lastStatusUpdatedTime;
    }

    public void setLastStatusUpdatedTime(Date lastStatusUpdatedTime) {
        this.lastStatusUpdatedTime = lastStatusUpdatedTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String getCookingInfo() {
        return cookingInfo;
    }

    public void setCookingInfo(String cookingInfo) {
        this.cookingInfo = cookingInfo;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "id=" + id +
                ", userModel=" + userModel +
                ", transactionModel=" + transactionModel +
                ", shopModel=" + shopModel +
                ", date=" + date +
                ", orderStatus=" + orderStatus +
                ", lastStatusUpdatedTime=" + lastStatusUpdatedTime +
                ", price=" + price +
                ", deliveryPrice=" + deliveryPrice +
                ", deliveryLocation='" + deliveryLocation + '\'' +
                ", cookingInfo='" + cookingInfo + '\'' +
                ", rating=" + rating +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}
