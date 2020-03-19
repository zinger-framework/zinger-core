package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.CollegeLogColumn.*;
import static com.food.ordering.zinger.column.ShopLogColumn.*;

import com.food.ordering.zinger.column.CollegeLogColumn;

import static com.food.ordering.zinger.column.ItemLogColumn.*;

public class AuditLogQuery {
	public static final String insertCollegeLog = "INSERT INTO" + CollegeLogColumn.tableName + "(" + CollegeLogColumn.id
			+ ", " + CollegeLogColumn.errorCode + ", " + CollegeLogColumn.mobile + ", " + CollegeLogColumn.message + ", " + CollegeLogColumn.updatedValue + ", " + CollegeLogColumn.date + ", " + CollegeLogColumn.priority
			+ ") VALUES(:" + CollegeLogColumn.id + ", :" + CollegeLogColumn.errorCode + " , :" + CollegeLogColumn.mobile + ", :" + CollegeLogColumn.message + ", :" + CollegeLogColumn.updatedValue + ", :"
			+ CollegeLogColumn.date + ", :" + CollegeLogColumn.priority + ")";

}