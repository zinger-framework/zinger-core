package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Time;

public class ShopModel {
    private Integer id;
    private String name;
    private String photoUrl;
    private String mobile;
    private CollegeModel collegeModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Kolkata")
    private Time openingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Kolkata")
    private Time closingTime;

    private Integer isDelete;

    public ShopModel() {
        this.collegeModel = new CollegeModel();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public CollegeModel getCollegeModel() {
        return collegeModel;
    }

    public void setCollegeModel(CollegeModel collegeModel) {
        this.collegeModel = collegeModel;
    }

    public Time getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Time openingTime) {
        this.openingTime = openingTime;
    }

    public Time getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Time closingTime) {
        this.closingTime = closingTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "ShopModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", mobile='" + mobile + '\'' +
                ", collegeModel=" + collegeModel +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                ", isDelete=" + isDelete +
                '}';
    }
}
