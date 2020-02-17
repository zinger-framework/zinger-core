package com.food.ordering.ssn.query;

public class LoginQuery {
	public static final String tableName = "users";
	public static final String isDelete = " is_delete = 0";
	
	public static final String insertUser = "INSERT INTO " + tableName + "(name, email, mobile, oauth_id, access_token, role) VALUES(:name, :email, :mobile, :oauth_id, :access_token, :role)";

	public static final String getAllUser = "SELECT id, name, email, mobile, oauth_id, access_token, role, is_delete FROM " + tableName + " WHERE" + isDelete;
	public static final String getUserById = "SELECT id, name, email, mobile, oauth_id, access_token, role, is_delete FROM " + tableName + " WHERE id = :id AND" + isDelete;
	public static final String getUserByOauthId = "SELECT id, name, email, mobile, oauth_id, access_token, role, is_delete FROM " + tableName + " WHERE oauth_id = :oauth_id AND" + isDelete;
	
	public static final String updateUserByOauthId = "UPDATE " + tableName + " SET name = :name, email = :email, mobile = :mobile WHERE oauth_id = :oauth_id AND" + isDelete;
	public static final String updateAccessTokenByOauthId = "UPDATE " + tableName + " SET access_token = :access_token WHERE oauth_id = :oauth_id AND" + isDelete;
	
	public static final String deleteUserByOauthId = "UPDATE " + tableName + " SET is_delete = 1 WHERE oauth_id = :oauth_id";
	
	public static final String validateUser = "SELECT id, name, email, mobile, oauth_id, access_token, role, is_delete FROM " + tableName + " WHERE oauth_id = :oauth_id AND access_token = :access_token AND" + isDelete;
}
