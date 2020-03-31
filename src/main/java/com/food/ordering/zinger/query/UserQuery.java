package com.food.ordering.zinger.query;

import com.food.ordering.zinger.column.UserShopColumn;

import static com.food.ordering.zinger.column.UserColumn.*;

public class UserQuery {
    public static final String notDeleted = isDelete + " = 0";

    public static final String insertUser = "INSERT INTO " + tableName + "(" + oauthId + ", " + mobile + ", " + role + ") VALUES( :" + oauthId + ", :" + mobile + ", :" + role + ")";
    public static final String insertSeller = "INSERT INTO " + tableName + "(" + mobile + ", " + role + ") VALUES( :" + mobile + ", :" + role + ")";

    public static final String getUserByMobile = "SELECT " + oauthId + ", " + name + ", " + email + ", " + mobile + ", " + role + " FROM " + tableName + " WHERE " + mobile + " = :" + mobile;
    public static final String loginUserByMobileOauth = getUserByMobile + " AND " + oauthId + " = :" + oauthId + " AND " + notDeleted;
    public static final String loginUserByMobileRole = getUserByMobile + " AND " + role + " = :" + role + " AND " + notDeleted;
    public static final String validateUser = loginUserByMobileRole + " AND " + oauthId + " = :" + oauthId;
    public static final String getSellerByShopId = "SELECT " + oauthId + ", " + name + ", " + email + ", " + mobile + ", " + role + " FROM " + tableName + " WHERE " + notDeleted + " AND " + mobile + " IN " + "(SELECT " + UserShopColumn.mobile + " FROM " + UserShopColumn.tableName + " WHERE " + UserShopColumn.shopId + " = :" + UserShopColumn.shopId + ")";

    public static final String updateUser = "UPDATE " + tableName + " SET " + name + " = :" + name + ", " + email + " = :" + email + " WHERE " + mobile + " = :" + mobile;
    public static final String updateOauthId = "UPDATE " + tableName + " SET " + oauthId + " = :" + oauthId + " WHERE " + mobile + " = :" + mobile;
    public static final String updateRole = "UPDATE " + tableName + " SET " + role + " = :" + role + " WHERE " + mobile + " = :" + mobile;
}
