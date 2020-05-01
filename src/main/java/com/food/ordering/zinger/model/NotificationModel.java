package com.food.ordering.zinger.model;

import com.food.ordering.zinger.constant.Enums;

public class NotificationModel {
    Enums.NotificationType type;
    String title;
    String message;
    String payload;


    public Enums.NotificationType getType() {
        return type;
    }

    public void setType(Enums.NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
