package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.OrderColumn.*;

public class OrderQuery {
    public static final String orderByDesc = " ORDER BY " + date + " DESC";

    public static final String insertOrder = "INSERT INTO " + tableName + "(" + mobile + ", " + transactionId + ", " + shopId + ", " + status + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ") VALUES(:" + mobile + ", :" + transactionId + ", :" + shopId + ", :" + status + ", :" + price + ", :" + deliveryPrice + ", :" + deliveryLocation + ", :" + cookingInfo + ")";

    public static final String getOrderByOrderId = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " WHERE " + id + " = :" + id;
    public static final String getOrderByMobile = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " WHERE " + mobile + " = :" + mobile + orderByDesc;
    public static final String getOrderByShopId = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " WHERE " + shopId + " = :" + shopId + orderByDesc;

    public static final String updateOrder = "UPDATE " + tableName + " SET " + cookingInfo + " = :" + cookingInfo + ", " + rating + " = :" + rating + ", " + secretKey + " = :" + secretKey + " WHERE " + id + " = :" + id;
    public static final String updateOrderStatus = "UPDATE " + tableName + " SET " + status + " = :" + status + ", " + lastStatusUpdatedTime + " = CURRENT_TIMESTAMP" + " WHERE " + id + " = :" + id;
}
