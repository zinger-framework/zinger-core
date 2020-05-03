package com.food.ordering.zinger.model;

import com.food.ordering.zinger.constant.Enums;

public class UserShopModel {
    private UserModel userModel;
    private ShopModel shopModel;
    private Enums.UserRole userRole;

    public UserShopModel() {
        userModel = new UserModel();
        shopModel = new ShopModel();
    }

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

    public Enums.UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(Enums.UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserShopModel{" +
                "userModel=" + userModel +
                ", shopModel=" + shopModel +
                ", userRole=" + userRole +
                '}';
    }
}
