package com.food.ordering.ssn.query;

public class ShopQuery {

	public static final String tableName = "shop";

	public static final String isDelete = " is_delete = 0";

	public static final String insertShop = "INSERT INTO " + tableName
			+ "(name, photo_url, mobile, college_id,opening_time,closing_time) VALUES(:name, :photo_url, :mobile, :college_id, :opening_time, :closing_time)";

	public static final String getAllShops = "SELECT id,name,college_id,photo_url,mobile,closing_time,opening_time,is_delete FROM "
			+ tableName + " WHERE" + isDelete;

	public static final String getShopByID = "SELECT id,name,college_id,photo_url,mobile,closing_time,opening_time,is_delete FROM "
			+ tableName + " WHERE id = :id AND" + isDelete;

	public static final String getShopsByCollegeID = "SELECT id,name,college_id,photo_url,mobile,closing_time,opening_time,is_delete FROM "
			+ tableName + " WHERE college_id = :college_id AND" + isDelete;

	public static final String updateShopByID = "UPDATE " + tableName
			+ " SET name = :name, photo_url = :photo_url, mobile = :mobile, opening_time = :opening_time, closing_time = :closing_time WHERE id = :id AND"
			+ isDelete;

	public static final String deleteShopByID = "UPDATE " + tableName + " SET is_delete = 1 WHERE id = :id";
}
