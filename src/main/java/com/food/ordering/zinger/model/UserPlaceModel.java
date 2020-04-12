package com.food.ordering.zinger.model;

public class UserPlaceModel {
    private UserModel userModel;
    private PlaceModel placeModel;

    public UserPlaceModel() {
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public PlaceModel getPlaceModel() {
        return placeModel;
    }

    public void setPlaceModel(PlaceModel placeModel) {
        this.placeModel = placeModel;
    }

    @Override
    public String toString() {
        return "UserCollegeModel{" +
                "userModel=" + userModel +
                ", collegeModel=" + placeModel +
                '}';
    }
}
