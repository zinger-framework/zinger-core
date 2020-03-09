package com.food.ordering.zinger.model;

public class ShopConfigurationModel {
    private ShopModel shopModel;
    private RatingModel ratingModel;
    private ConfigurationModel configurationModel;

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

    @Override
    public String toString() {
        return "ShopConfigurationModel{" +
                "shopModel=" + shopModel +
                ", ratingModel=" + ratingModel +
                ", configurationModel=" + configurationModel +
                '}';
    }
}
