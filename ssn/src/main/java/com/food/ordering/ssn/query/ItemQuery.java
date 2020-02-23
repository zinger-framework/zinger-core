package com.food.ordering.ssn.query;

public class ItemQuery {
	public static final String tableName = "item";

	public static final String isDelete = " is_delete = 0";

	public static final String getItemByID = "SELECT id,name,price,photo_url,category,shop_id,is_veg,is_available,is_delete FROM "
			+ tableName + " WHERE id = :id AND" + isDelete;

	public static final String getItemsByShopID = "SELECT id,name,price,photo_url,category,shop_id,is_veg,is_available,is_delete FROM "
			+ tableName + " WHERE shop_id = :shop_id AND" + isDelete;

	public static final String getItemsByQuery = "SELECT id,name,price,photo_url,category,shop_id,is_veg,is_available,is_delete FROM "
			+ tableName + " WHERE shop_id = :shop_id AND name LIKE :query";

}
