package com.food.ordering.ssn.query;

public class UserShopQuery {

	public static final String tableName = "users_shop";
	
	public static final String insertObject = "INSERT INTO " + tableName + "(oauth_id,shop_id) VALUES( :oauth_id, :shop_id)";

	public static final String updateUserCollege = "UPDATE " + tableName + " SET oauth_id = :oauth_id, shop_id = :shop_id WHERE oauth_id = :oauth_id AND shop_id = :shop_id";

}
