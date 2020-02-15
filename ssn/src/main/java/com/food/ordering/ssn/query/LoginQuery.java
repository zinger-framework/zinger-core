package com.food.ordering.ssn.query;

public class LoginQuery {
	public static final String tableName = "users";
	public static final String isDelete = " is_delete = 0";
	
	public static final String getAllUser = "SELECT id, name, email, mobile, oauth_id, access_token, role, is_delete FROM " + tableName + " WHERE" + isDelete;
	public static final String getUserById = "SELECT id, name, email, mobile, oauth_id, access_token, role, is_delete FROM " + tableName + " WHERE id = :id AND" + isDelete;
	
	public static final String insertUser = "INSERT INTO " + tableName + "(name, email, mobile, oauth_id, access_token, role) VALUES(:name, :email, :mobile, :oauth_id, :access_token, :role)";
	public static final String updateUserById = "UPDATE " + tableName + " SET name = :name, email = :email, mobile = :mobile, access_token = :access_token WHERE id = :id AND" + isDelete;
	public static final String deleteUserById = "UPDATE " + tableName + " SET is_delete = 1 WHERE id = :id";
}
