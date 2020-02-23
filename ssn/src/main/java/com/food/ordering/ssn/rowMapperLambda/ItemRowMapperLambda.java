package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.ItemModel;

public class ItemRowMapperLambda {
	public static final RowMapper<ItemModel> itemRowMapperLambda = (rs,rownum) -> {
		ItemModel item = new ItemModel();
		item.setID(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setPrice(rs.getDouble("price"));
		item.setPhotoUrl(rs.getString("photo_url"));
		item.setCategory(rs.getString("category"));
		item.setShopId(rs.getInt("shop_id"));
		item.setIsVeg(rs.getInt("is_veg"));
		item.setIsAvailable(rs.getInt("is_available"));
		item.setIsDelete(rs.getInt("is_delete"));
		return item;
	};
}
