package com.food.ordering.zinger.model;

public class SellerLoginResponse {
    private UserModel userModel;
    private ShopModel shopModel;
    private ConfigurationModel configurationModel;
    private RatingModel ratingModel;

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

    public ConfigurationModel getConfigurationModel() {
        return configurationModel;
    }

    public void setConfigurationModel(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    public RatingModel getRatingModel() {
        return ratingModel;
    }

    public void setRatingModel(RatingModel ratingModel) {
        this.ratingModel = ratingModel;
    }

    @Override
    public String toString() {
        return "SellerLoginResponse{" +
                "userModel=" + userModel +
                ", shopModel=" + shopModel +
                ", configurationModel=" + configurationModel +
                ", ratingModel=" + ratingModel +
                '}';
    }
}
