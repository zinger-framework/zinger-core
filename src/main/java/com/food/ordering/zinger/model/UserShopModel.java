package com.food.ordering.zinger.model;

public class UserShopModel {
    private UserModel userModel;
    private ShopModel shopModel;
    private Integer isDelete;

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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "UserShopModel{" +
                "userModel=" + userModel +
                ", shopModel=" + shopModel +
                ", isDelete=" + isDelete +
                '}';
    }
}
