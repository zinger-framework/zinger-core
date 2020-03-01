package com.food.ordering.ssn.model;

public class RatingModel {
    private ShopModel shopModel;
    private Double rating;
    private Integer userCount;

    public RatingModel() {
        shopModel = new ShopModel();
    }

    public ShopModel getShopModel() {
        return shopModel;
    }

    public void setShopModel(ShopModel shopModel) {
        this.shopModel = shopModel;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return "RatingModel{" +
                "shopModel=" + shopModel +
                ", rating=" + rating +
                ", userCount=" + userCount +
                '}';
    }
}
