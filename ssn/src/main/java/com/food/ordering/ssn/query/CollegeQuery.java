package com.food.ordering.ssn.query;

public class CollegeQuery {
	public static final String tableName = "college";
	
	public static final String isDelete = " is_delete = 0";
	
	public static final String getAllColleges = "SELECT id,name,icon_url,address,is_delete FROM " + tableName + " WHERE" + isDelete;
	
	public static final String getCollegeById = "SELECT id,name,icon_url,address,is_delete FROM " + tableName + " WHERE id = :id AND" + isDelete;

	public static final String updateCollege = "UPDATE " + tableName + " SET name = :name, icon_url = :icon_url, address = :address WHERE id = :id AND" + isDelete;

}
