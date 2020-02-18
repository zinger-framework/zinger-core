package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.model.UserModel;

public class RowMapperLambda {
	public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
		UserModel userModel = new UserModel();
		userModel.setId(rs.getInt("id"));
		userModel.setName(rs.getString("name"));
		userModel.setEmail(rs.getString("email"));
		userModel.setAccessToken(rs.getString("access_token"));
		userModel.setOauthId(rs.getString("oauth_id"));
		userModel.setMobile(rs.getString("mobile"));
		userModel.setIsDelete(rs.getInt("is_delete"));
		userModel.setRole(rs.getString("role"));
		return userModel;
	};
	
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
	
	public static final RowMapper<CollegeModel> collegeRowMapperLambda = (rs, rownum) -> {
		CollegeModel college = new CollegeModel();
		college.setID(rs.getInt("id"));
		college.setName(rs.getString("name"));
		college.setIconUrl(rs.getString("icon_url"));
		college.setAddress(rs.getString("address"));
		college.setIsDelete(rs.getInt("is_delete"));
		return college;
	};
}
