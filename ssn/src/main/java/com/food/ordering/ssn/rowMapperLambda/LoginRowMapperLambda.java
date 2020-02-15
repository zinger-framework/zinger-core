package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.UserModel;

public class LoginRowMapperLambda {
	public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
		UserModel userModel = new UserModel();
		userModel.setId(rs.getInt("id"));
		userModel.setName(rs.getString("name"));
		//TODO: FINISH ALL
		return userModel;
	};
}
