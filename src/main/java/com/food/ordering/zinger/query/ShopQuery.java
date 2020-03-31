package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.ShopColumn.*;

public class ShopQuery {
    public static final String notDeleted = isDelete + " = 0";

    public static final String insertShop = "INSERT INTO " + tableName + " (" + name + ", " + photoUrl + ", " + mobile + ", " + collegeId + ", " + openingTime + ", " + closingTime + ") VALUES(:" + name + ", :" + photoUrl + ", :" + mobile + ", :" + collegeId + ", :" + openingTime + ", :" + closingTime + ")";

    public static final String getShopByCollegeId = "SELECT " + id + ", " + name + ", " + photoUrl + ", " + mobile + ", " + collegeId + ", " + openingTime + ", " + closingTime + " FROM " + tableName + " WHERE " + collegeId + " = :" + collegeId + " AND " + notDeleted;
    public static final String getShopIdByCollegeId = "SELECT " + id + " FROM " + tableName + " WHERE " + collegeId + " = :" + collegeId + " AND " + notDeleted;
    public static final String getShopById = "SELECT " + id + ", " + name + ", " + photoUrl + ", " + mobile + ", " + collegeId + ", " + openingTime + ", " + closingTime + " FROM " + tableName + " WHERE " + id + " = :" + id;

    public static final String updateShop = "UPDATE " + tableName + " SET " + name + " = :" + name + ", " + photoUrl + " = :" + photoUrl + ", " + mobile + " = :" + mobile + ", " + openingTime + " = :" + openingTime + ", " + closingTime + " = :" + closingTime + " WHERE " + id + " = :" + id;

    public static final String deleteShop = "UPDATE " + tableName + " SET " + isDelete + " = 1" + " WHERE " + id + " = :" + id;
    public static final String unDeleteShop = "UPDATE " + tableName + " SET " + isDelete + " = 0" + " WHERE " + id + " = :" + id;
}
