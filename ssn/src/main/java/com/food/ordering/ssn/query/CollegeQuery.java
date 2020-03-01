package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.CollegeColumn.*;

public class CollegeQuery {
    public static final String notDeleted = isDelete + " = 0";

    public static final String insertCollege = "INSERT INTO " + tableName + "(" + name + ", " + iconUrl + ", " + address + ") VALUES(:" + name + ", : " + iconUrl + " , :" + address + ")";

    public static final String getAllColleges = "SELECT " + id + ", " + name + ", " + iconUrl + ", " + address + ", " + isDelete + " FROM " + tableName + " WHERE " + notDeleted + " ORDER BY " + name + " ASC";
    public static final String getCollegeById = "SELECT " + id + ", " + name + ", " + iconUrl + ", " + address + ", " + isDelete + " FROM " + tableName + " WHERE " + id + " = :" + id;

    public static final String updateCollege = "UPDATE " + tableName + " SET " + name + " = :" + name + ", " + iconUrl + " = :" + iconUrl + ", " + address + " = :" + address + " WHERE " + id + " = :" + id;

    public static final String deleteCollege = "UPDATE " + tableName + " SET " + isDelete + " = 1" + " WHERE " + id + " = :" + id;
    public static final String unDeleteCollege = "UPDATE " + tableName + " SET " + isDelete + " = 0" + " WHERE " + id + " = :" + id;
}
