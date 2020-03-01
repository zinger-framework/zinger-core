package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.OrderColumn.*;

public class OrderQuery {
    public static final String orderByDesc = " ORDER BY " + date + " DESC";

    public static final String insertOrder = "INSERT INTO " + tableName + "(" + oauthId + ", " + transactionId + ", " + shopId + ", " + status + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ") VALUES(:" + oauthId + ", :" + transactionId + ", :" + shopId + ", :" + status + ", :" +  price + ", :" + deliveryPrice + ", :" + deliveryLocation + ", :" + cookingInfo + ")";

    public static final String getOrderByOauthId = "SELECT " + id + ", " + oauthId + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " WHERE " + oauthId + " = :" + oauthId + orderByDesc;
    public static final String getOrderByShopId = "SELECT " + id + ", " + oauthId + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " WHERE " + shopId + " = :" + shopId + orderByDesc;

    public static final String updateOrder = "UPDATE " + tableName + " SET " + cookingInfo + " = :" + cookingInfo + ", " + rating + " = :" + rating + ", " + secretKey + " = :" + secretKey + " WHERE " + id + " = :" + id;
    public static final String updateOrderStatus = "UPDATE " + tableName + " SET " + status + " = :" + status + ", " + lastStatusUpdatedTime + " = CURRENT_TIMESTAMP" + " WHERE " + id + " = :" + id;
}
