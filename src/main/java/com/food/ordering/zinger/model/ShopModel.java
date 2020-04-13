package com.food.ordering.zinger.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Time;
import java.util.List;

public class ShopModel {
    private Integer id;
    private String name;
    private String photoUrl;
    private List<String> coverUrls;
    private String mobile;
    private PlaceModel placeModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Kolkata")
    private Time openingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Kolkata")
    private Time closingTime;

    public ShopModel() {
        this.placeModel = new PlaceModel();
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

    public PlaceModel getPlaceModel() {
        return placeModel;
    }

    public void setPlaceModel(PlaceModel placeModel) {
        this.placeModel = placeModel;
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

    public List<String> getCoverUrls() {
        return coverUrls;
    }

    public String toFormattedCoverUrls() {
        String result = "[";
        for (int i = 0; i < coverUrls.size(); i++) {
            result += "\"" + coverUrls.get(i) + "\"";
            if (i < coverUrls.size() - 1)
                result += ",";
        }
        result += "]";
        return result;
    }

    public void setCoverUrls(List<String> coverUrls) {
        this.coverUrls = coverUrls;
    }

    @Override
    public String toString() {
        return "ShopModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", coverUrls=" + coverUrls +
                ", mobile='" + mobile + '\'' +
                ", placeModel=" + placeModel +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                '}';
    }
}
