package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.UserShopColumn.*;

public class UserShopQuery {
    public static final String insertUserShop = "INSERT INTO " + tableName + "(" + mobile + "," + shopId + ") VALUES(:" + mobile + ", :" + shopId + ")";

    public static final String getUserByShopId = "SELECT " + mobile + ", " + shopId + " WHERE " + shopId + " = :" + shopId;
    public static final String getShopByMobile = "SELECT " + mobile + ", " + shopId + " WHERE " + mobile + " = :" + mobile;

    public static final String deleteUser = "DELETE FROM " + tableName + " WHERE " + mobile + " = :" + mobile + " AND " + shopId + " = :" + shopId;
}
