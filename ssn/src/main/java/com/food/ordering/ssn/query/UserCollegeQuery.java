package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.UserCollegeColumn.*;

public class UserCollegeQuery {
    public static final String insertUserCollege = "INSERT INTO " + tableName + "(" + oauthId + "," + collegeId + ") VALUES(:" + oauthId + ", :" + collegeId + ")";

    public static final String getCollegeByOauthId = "SELECT " + oauthId + ", " + collegeId + " WHERE " + oauthId + " = :" + oauthId;

    public static final String updateCollegeByOauthId = "UPDATE " + tableName + " SET " + collegeId + " = :" + collegeId + " WHERE " + oauthId + " = :" + oauthId;
}
