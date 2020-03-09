package com.food.ordering.zinger.model;

import java.util.ArrayList;
import java.util.List;

public class UserShopListModel {
    private UserModel userModel;
    private List<ShopConfigurationModel> shopModelList;

    public UserShopListModel() {
        userModel = new UserModel();
        shopModelList = new ArrayList<>();
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public List<ShopConfigurationModel> getShopModelList() {
        return shopModelList;
    }

    public void setShopModelList(List<ShopConfigurationModel> shopModelList) {
        this.shopModelList = shopModelList;
    }

    @Override
    public String toString() {
        return "UserShopListModel{" +
                "userModel=" + userModel +
                ", shopModelList=" + shopModelList +
                '}';
    }
}
