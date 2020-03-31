package com.food.ordering.zinger.model;

public class UserShopModel {
    private UserModel userModel;
    private ShopModel shopModel;

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

    @Override
    public String toString() {
        return "UserShopModel{" +
                "userModel=" + userModel +
                ", shopModel=" + shopModel +
                '}';
    }
}
