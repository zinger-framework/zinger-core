package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.UserColumn.*;

public class UserQuery {
    public static final String notDeleted = isDelete + " = 0";

    public static final String insertUser = "INSERT INTO " + tableName + "(" + oauthId + ", " + name + ", " + email + ", " + mobile + ", " + role + ") VALUES( :" + oauthId + ",:" + name + ", :" + email + ", :" + mobile + ", :" + role + ")";

    public static final String getUserByOauthId = "SELECT " + oauthId + ", " + name + ", " + email + ", " + mobile + ", " + role + ", " + isDelete + " FROM " + tableName + " WHERE " + oauthId + " = :" + oauthId;
    public static final String validateUser = "SELECT " + oauthId + ", " + name + ", " + email + ", " + mobile + ", " + role + ", " + isDelete + " FROM " + tableName + " WHERE " + oauthId + " = :" + oauthId + " AND " + notDeleted;

    public static final String updateUser = "UPDATE " + tableName + " SET " + name + " = :" + name + ", " + email + " = :" + email + ", " + mobile + " = :" + mobile + ", " + role + " = :" + role + " WHERE " + oauthId + " = :" + oauthId;

    public static final String deleteUser = "UPDATE " + tableName + " SET " + isDelete + " = 1" + " WHERE " + oauthId + " = :" + oauthId;
    public static final String unDeleteUser = "UPDATE " + tableName + " SET " + isDelete + " = 0" + " WHERE " + oauthId + " = :" + oauthId;
}
