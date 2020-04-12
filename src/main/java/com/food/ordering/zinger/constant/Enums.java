package com.food.ordering.zinger.constant;

public class Enums {
    public static enum OrderStatus {
        PENDING, TXN_FAILURE, PLACED, CANCELLED_BY_USER, ACCEPTED, CANCELLED_BY_SELLER, READY, OUT_FOR_DELIVERY, COMPLETED, DELIVERED
    }

    public static enum Priority {
        LOW, MEDIUM, HIGH
    }

    public static enum UserRole {
        CUSTOMER, SELLER, SHOP_OWNER, SUPER_ADMIN
    }
}
