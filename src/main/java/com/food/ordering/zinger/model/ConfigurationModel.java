package com.food.ordering.zinger.model;

public class ConfigurationModel {
    private ShopModel shopModel;
    private Double deliveryPrice;
    private Integer isDeliveryAvailable;
    private Integer isOrderTaken;

    public ConfigurationModel() {
        shopModel = new ShopModel();
    }

    public ShopModel getShopModel() {
        return shopModel;
    }

    public void setShopModel(ShopModel shopModel) {
        this.shopModel = shopModel;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Integer getIsDeliveryAvailable() {
        return isDeliveryAvailable;
    }

    public void setIsDeliveryAvailable(Integer isDeliveryAvailable) {
        this.isDeliveryAvailable = isDeliveryAvailable;
    }

    public Integer getIsOrderTaken() {
        return isOrderTaken;
    }

    public void setIsOrderTaken(Integer isOrderTaken) {
        this.isOrderTaken = isOrderTaken;
    }

    @Override
    public String toString() {
        return "ConfigurationModel{" +
                "shopModel=" + shopModel +
                ", deliveryPrice=" + deliveryPrice +
                ", isDeliveryAvailable=" + isDeliveryAvailable +
                ", isOrderTaken=" + isOrderTaken +
                '}';
    }
}
