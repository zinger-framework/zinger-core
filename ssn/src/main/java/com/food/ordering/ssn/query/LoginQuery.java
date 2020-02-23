package com.food.ordering.ssn.query;

public class LoginQuery {
	public static final String tableName = "users";
	public static final String isDelete = " is_delete = 0";
	
	public static final String insertUser = "INSERT INTO " + tableName + "(oauth_id,name, email, mobile, role) VALUES( :oauth_id,:name, :email, :mobile, :role)";

	public static final String getAllUser = "SELECT  oauth_id, name, email, mobile, role, is_delete FROM " + tableName + " WHERE" + isDelete;
	public static final String getUserById = "SELECT  oauth_id, name, email, mobile, role, is_delete FROM " + tableName + " WHERE oauth_id = :oauth_id AND" + isDelete;
	public static final String getUserByOauthId = "SELECT  oauth_id, name, email, mobile, role, is_delete FROM " + tableName + " WHERE oauth_id = :oauth_id AND" + isDelete;
	
	public static final String updateUserByOauthId = "UPDATE " + tableName + " SET name = :name, email = :email, mobile = :mobile WHERE oauth_id = :oauth_id AND" + isDelete;
	
	public static final String deleteUserByOauthId = "UPDATE " + tableName + " SET is_delete = 1 WHERE oauth_id = :oauth_id";
	
	public static final String validateUser = "SELECT  oauth_id, name, email, mobile, role, is_delete FROM " + tableName + " WHERE oauth_id = :oauth_id AND" + isDelete;
}
