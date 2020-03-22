package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.UserColumn.*;

public class UserQuery {
    public static final String notDeleted = isDelete + " = 0";

    public static final String insertUser = "INSERT INTO " + tableName + "(" + oauthId + ", " + mobile + ", " + role + ") VALUES( :" + oauthId + ",:" + mobile + ", :" + role + ")";
    public static final String insertSeller = "INSERT INTO " + tableName + "(" + mobile + ", " + role + ") VALUES( :" + mobile + ", :" + role + ")";

    public static final String getUserByMobile = "SELECT " + oauthId + ", " + name + ", " + email + ", " + mobile + ", " + role + ", " + isDelete + " FROM " + tableName + " WHERE " + mobile + " = :" + mobile;
    public static final String loginUserByMobile = getUserByMobile + " AND " + role + " = :" + role + " AND " + notDeleted;
    public static final String validateUser = loginUserByMobile + " AND " + oauthId + " = :" + oauthId;

    public static final String updateUser = "UPDATE " + tableName + " SET " + name + " = :" + name + ", " + email + " = :" + email + " WHERE " + mobile + " = :" + mobile;
    public static final String updateOauthId = "UPDATE " + tableName + " SET " + oauthId + " = :" + oauthId + " WHERE " + mobile + " = :" + mobile;

    public static final String deleteUser = "UPDATE " + tableName + " SET " + isDelete + " = 1" + " WHERE " + oauthId + " = :" + oauthId;
    public static final String unDeleteUser = "UPDATE " + tableName + " SET " + isDelete + " = 0" + " WHERE " + oauthId + " = :" + oauthId;
}
