package com.food.ordering.zinger.model;

import com.food.ordering.zinger.constant.Enums.UserRole;

import java.util.List;

public class UserModel {
    private Integer id;
    private String mobile;
    private String name;
    private String email;
    private String oauthId;
    private List<String> notificationToken;
    private UserRole role;

    public UserModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<String> getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(List<String> notificationToken) {
        this.notificationToken = notificationToken;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", oauthId='" + oauthId + '\'' +
                ", notificationToken='" + notificationToken + '\'' +
                ", role=" + role +
                '}';
    }
}
