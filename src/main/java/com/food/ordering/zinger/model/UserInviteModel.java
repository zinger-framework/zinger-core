package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class UserInviteModel {
    private UserModel userModel;
    private ShopModel shopModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp date;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public ShopModel getShopModel() {
        return shopModel;
    }

    public void setShopModel(ShopModel shopModel) {
        this.shopModel = shopModel;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserInviteModel{" +
                "userModel=" + userModel +
                ", shopModel=" + shopModel +
                ", date=" + date +
                '}';
    }
}
