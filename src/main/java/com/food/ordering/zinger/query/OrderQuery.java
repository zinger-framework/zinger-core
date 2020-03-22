package com.food.ordering.zinger.query;

import com.food.ordering.zinger.enums.OrderStatus;

import static com.food.ordering.zinger.column.OrderColumn.*;

public class OrderQuery {
    public static final String Limit = " LIMIT ";
    public static final String Offset = " OFFSET ";
    public static final String orderByDesc = " ORDER BY " + date + " DESC";
    public static final String pageNum = "pageNum";
    public static final String pageCount = "pageCount";

    public static final String insertOrder = "INSERT INTO " + tableName + "(" + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + status + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ") VALUES(:" + id + ", :" + mobile + ", :" + transactionId + ", :" + shopId + ", :" + status + ", :" + price + ", :" + deliveryPrice + ", :" + deliveryLocation + ", :" + cookingInfo + ")";

    public static final String getOrderByOrderId = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " FROM " +  tableName + " WHERE " + id + " = :" + id;
    public static final String getOrderByMobile = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " FROM " +  tableName + " WHERE " + mobile + " = :" + mobile + orderByDesc + Limit + ":" + pageCount + Offset + ":" + pageNum;
    public static final String getOrderByShopIdPagination = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " FROM " +  tableName + " WHERE " + shopId + " = :" + shopId + orderByDesc + Limit + ":" + pageCount + Offset + ":" + pageNum;
    public static final String getOrderByShopId = "SELECT " + id + ", " + mobile + ", " + transactionId + ", " + shopId + ", " + date + ", " + status + ", " + lastStatusUpdatedTime + ", " + price + ", " + deliveryPrice + ", " + deliveryLocation + ", " + cookingInfo + ", " + rating + ", " + secretKey + " FROM " +  tableName + " WHERE " + shopId + " = :" + shopId + " AND (" + status + " = '" + OrderStatus.PLACED.name() + "' || " + status + " = '" + OrderStatus.ACCEPTED.name() + "' || " + status + " = '" + OrderStatus.READY.name() + "' || " + status + " = '" + OrderStatus.OUT_FOR_DELIVERY.name() + "')" + orderByDesc;

    public static final String updateOrderRating = "UPDATE " + tableName + " SET " + rating + " = :" + rating + " WHERE " + id + " = :" + id;
    public static final String updateOrderKey = "UPDATE " + tableName + " SET " + secretKey + " = :" + secretKey + " WHERE " + id + " = :" + id;
    public static final String updateOrderStatus = "UPDATE " + tableName + " SET " + status + " = :" + status + ", " + lastStatusUpdatedTime + " = CURRENT_TIMESTAMP" + " WHERE " + id + " = :" + id;
}
