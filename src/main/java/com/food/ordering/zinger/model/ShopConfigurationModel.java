package com.food.ordering.zinger.model;

import com.food.ordering.zinger.constant.Enums;

public class ShopConfigurationModel {
    private ShopModel shopModel;
    private RatingModel ratingModel;
    private ConfigurationModel configurationModel;
    private Enums.UserRole userRole;

    public ShopModel getShopModel() {
        return shopModel;
    }

    public void setShopModel(ShopModel shopModel) {
        this.shopModel = shopModel;
    }

    public RatingModel getRatingModel() {
        return ratingModel;
    }

    public void setRatingModel(RatingModel ratingModel) {
        this.ratingModel = ratingModel;
    }

    public ConfigurationModel getConfigurationModel() {
        return configurationModel;
    }

    public void setConfigurationModel(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    public Enums.UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(Enums.UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "ShopConfigurationModel{" +
                "shopModel=" + shopModel +
                ", ratingModel=" + ratingModel +
                ", configurationModel=" + configurationModel +
                ", userRole=" + userRole +
                '}';
    }
}
