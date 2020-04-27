package com.food.ordering.zinger.constant;

public class Enums {
    public enum OrderStatus {
        PENDING, TXN_FAILURE, PLACED, CANCELLED_BY_USER, ACCEPTED, CANCELLED_BY_SELLER, READY, OUT_FOR_DELIVERY, COMPLETED, DELIVERED, REFUND_INITIATED, REFUND_COMPLETED
    }

    public enum TransactionStatus {
        PENDING, TXN_FAILURE, TXN_SUCCESS, REFUND_COMPLETED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum UserRole {
        CUSTOMER, SELLER, SHOP_OWNER, DELIVERY, SUPER_ADMIN
    }

    public enum NotificationType{
        URL,ORDER_STATUS,NEW_ARRIVAL,NEW_ORDER,ORDER_CANCELLED,NEW_SELLER
    }
}
