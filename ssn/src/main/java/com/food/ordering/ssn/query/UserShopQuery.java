package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.UserShopColumn.*;

public class UserShopQuery {
    public static final String insertUserShop = "INSERT INTO " + tableName + "(" + oauthId + "," + shopId + ") VALUES(:" + oauthId + ", :" + shopId + ")";

    public static final String getUserByShopId = "SELECT " + oauthId + ", " + shopId + " WHERE " + shopId + " = :" + shopId;
    public static final String getShopByOauthId = "SELECT " + oauthId + ", " + shopId + " WHERE " + oauthId + " = :" + oauthId;

    public static final String deleteUser = "DELETE FROM " + tableName + " WHERE " + oauthId + " = :" + oauthId + " AND " + shopId + " = :" + shopId;
}
