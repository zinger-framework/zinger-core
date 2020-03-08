package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.UserCollegeColumn.*;

public class UserCollegeQuery {
    public static final String insertUserCollege = "INSERT INTO " + tableName + "(" + mobile + "," + collegeId + ") VALUES(:" + mobile + ", :" + collegeId + ")";

    public static final String getCollegeByMobile = "SELECT " + mobile + ", " + collegeId + " FROM " + tableName + " WHERE " + mobile + " = :" + mobile;

    public static final String updateCollegeByMobile = "UPDATE " + tableName + " SET " + collegeId + " = :" + collegeId + " WHERE " + mobile + " = :" + mobile;
}
