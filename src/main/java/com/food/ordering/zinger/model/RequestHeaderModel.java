package com.food.ordering.zinger.model;

public class RequestHeaderModel {
    String oauthId;
    String mobile;
    String role;

    public RequestHeaderModel() {
    }

    public RequestHeaderModel(String oauthId, String mobile, String role) {
        this.oauthId = oauthId;
        this.mobile = mobile;
        this.role = role;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "ResponseHeaderModel{" +
                "oauthId='" + oauthId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
