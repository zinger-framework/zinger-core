package com.food.ordering.zinger.model;

public class ConfigurationModel {
    private ShopModel shopModel;
    private String merchantId;
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

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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
                ", merchantId='" + merchantId + '\'' +
                ", deliveryPrice=" + deliveryPrice +
                ", isDeliveryAvailable=" + isDeliveryAvailable +
                ", isOrderTaken=" + isOrderTaken +
                '}';
    }
}
