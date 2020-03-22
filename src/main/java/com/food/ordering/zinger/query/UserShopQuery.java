package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.UserShopColumn.*;

public class UserShopQuery {
    public static final String insertUserShop = "INSERT INTO " + tableName + "(" + mobile + "," + shopId + ") VALUES(:" + mobile + ", :" + shopId + ")";

    public static final String getUserByShopId = "SELECT " + mobile + ", " + shopId + " FROM " + tableName + " WHERE " + shopId + " = :" + shopId;
    public static final String getShopByMobile = "SELECT " + mobile + ", " + shopId + " FROM " + tableName + " WHERE " + mobile + " = :" + mobile;

    public static final String updateShopByMobile = "UPDATE " + tableName + " SET " + shopId + " = :" + shopId + " WHERE " + mobile + " = :" + mobile;

    public static final String deleteUser = "DELETE FROM " + tableName + " WHERE " + mobile + " = :" + mobile + " AND " + shopId + " = :" + shopId;
}
