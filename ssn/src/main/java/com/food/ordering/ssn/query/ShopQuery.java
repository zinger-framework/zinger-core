package com.food.ordering.ssn.query;

public class ShopQuery {

	public static final String tableName = "shop";

	public static final String isDelete = " is_delete = 0";

	public static final String getShopsByCollegeID = "SELECT id,name,college_id,photo_url,mobile,closing_time,opening_time,is_delete FROM "
			+ tableName + " WHERE college_id = :college_id AND" + isDelete;

	public static final String getAllShops = "SELECT id,name,college_id,photo_url,mobile,closing_time,opening_time,is_delete FROM "
			+ tableName + "WHERE " + isDelete;
}
