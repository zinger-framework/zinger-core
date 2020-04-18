package com.food.ordering.zinger.model;

public class RequestHeaderModel {
    String oauthId;
    Integer id;
    String role;

    public RequestHeaderModel() {
    }

    public RequestHeaderModel(String oauthId, Integer id, String role) {
        this.oauthId = oauthId;
        this.id = id;
        this.role = role;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RequestHeaderModel{" +
                "oauthId='" + oauthId + '\'' +
                ", id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
