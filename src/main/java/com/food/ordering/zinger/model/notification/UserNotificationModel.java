package com.food.ordering.zinger.model.notification;

public class UserNotificationModel {
    private Integer id;
    private String notificationToken;

    public UserNotificationModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    @Override
    public String toString() {
        return "UserNotificationModel{" +
                "id=" + id +
                ", notificationToken='" + notificationToken + '\'' +
                '}';
    }
}
