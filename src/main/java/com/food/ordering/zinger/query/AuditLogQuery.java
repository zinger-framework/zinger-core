package com.food.ordering.zinger.query;

import com.food.ordering.zinger.column.logger.*;

public class AuditLogQuery {
    public static final String insertCollegeLog = "INSERT INTO " + CollegeLogColumn.tableName + "(" + CollegeLogColumn.id
            + ", " + CollegeLogColumn.errorCode + ", " + CollegeLogColumn.mobile + ", " + CollegeLogColumn.message + ", " + CollegeLogColumn.updatedValue + ", " + CollegeLogColumn.priority
            + ") VALUES(:" + CollegeLogColumn.id + ", :" + CollegeLogColumn.errorCode + " , :" + CollegeLogColumn.mobile + ", :" + CollegeLogColumn.message + ", :" + CollegeLogColumn.updatedValue + ", :"
            + CollegeLogColumn.priority + ")";

    public static final String insertShopLog = "INSERT INTO " + ShopLogColumn.tableName + "(" + ShopLogColumn.id
            + ", " + ShopLogColumn.errorCode + ", " + ShopLogColumn.mobile + ", " + ShopLogColumn.message + ", " + ShopLogColumn.updatedValue + ", " + ShopLogColumn.priority
            + ") VALUES(:" + ShopLogColumn.id + ", :" + ShopLogColumn.errorCode + " , :" + ShopLogColumn.mobile + ", :" + ShopLogColumn.message + ", :" + ShopLogColumn.updatedValue + ", :"
            + ShopLogColumn.priority + ")";

    public static final String insertUserLog = "INSERT INTO " + UserLogColumn.tableName + "(" + UserLogColumn.usersMobile
            + ", " + UserLogColumn.errorCode + ", " + UserLogColumn.mobile + ", " + UserLogColumn.message + ", " + UserLogColumn.updatedValue + ", " + UserLogColumn.priority
            + ") VALUES(:" + UserLogColumn.usersMobile + ", :" + UserLogColumn.errorCode + " , :" + UserLogColumn.mobile + ", :" + UserLogColumn.message + ", :" + UserLogColumn.updatedValue + ", :"
            + UserLogColumn.priority + ")";

    public static final String insertItemLog = "INSERT INTO " + ItemLogColumn.tableName + "(" + ItemLogColumn.id
            + ", " + ItemLogColumn.errorCode + ", " + ItemLogColumn.mobile + ", " + ItemLogColumn.message + ", " + ItemLogColumn.updatedValue + ", " + ItemLogColumn.priority
            + ") VALUES(:" + ItemLogColumn.id + ", :" + ItemLogColumn.errorCode + " , :" + ItemLogColumn.mobile + ", :" + ItemLogColumn.message + ", :" + ItemLogColumn.updatedValue + ", :"
            + ItemLogColumn.priority + ")";

    public static final String insertOrderLog = "INSERT INTO " + OrderLogColumn.tableName + "(" + OrderLogColumn.id
            + ", " + OrderLogColumn.errorCode + ", " + OrderLogColumn.mobile + ", " + OrderLogColumn.message + ", " + OrderLogColumn.updatedValue + ", " + OrderLogColumn.priority
            + ") VALUES(:" + OrderLogColumn.id + ", :" + OrderLogColumn.errorCode + " , :" + OrderLogColumn.mobile + ", :" + OrderLogColumn.message + ", :" + OrderLogColumn.updatedValue + ", :"
            + OrderLogColumn.priority + ")";

}
