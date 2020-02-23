package com.food.ordering.ssn.query;

public class UsersCollegeQuery {

	public static final String tableName = "users_college";
	
	public static final String insertObject = "INSERT INTO " + tableName + "(oauth_id,college_id) VALUES( :oauth_id, :college_id)";
	
	public static final String updateUserCollege = "UPDATE " + tableName + " SET oauth_id = :oauth_id, college_id = :college_id WHERE oauth_id = :oauth_id";
}
