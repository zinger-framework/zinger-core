package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.ShopModel;

public class ShopRowMapperLambda {
	public static final RowMapper<ShopModel> shopRowMapperLambda = (rs,rownum) -> {
		ShopModel shop = new ShopModel();
		shop.setID(rs.getInt("id"));
		shop.setName(rs.getString("name"));
		shop.setMobile(rs.getString("mobile"));
		shop.setCollegeId(rs.getInt("college_id"));
		shop.setIsDelete(rs.getInt("is_delete"));
		shop.setClosingTime(rs.getTime("closing_time"));
		shop.setOpeningTime(rs.getTime("opening_time"));
		return shop;
	};
	
}
