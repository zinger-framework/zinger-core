package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.UserModel;

public class LoginRowMapperLambda {
	public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
		UserModel userModel = new UserModel();
		userModel.setOauthId(rs.getString("oauth_id"));
		userModel.setName(rs.getString("name"));
		userModel.setEmail(rs.getString("email"));
		userModel.setMobile(rs.getString("mobile"));
		userModel.setRole(rs.getString("role"));
		userModel.setIsDelete(rs.getInt("is_delete"));
		return userModel;
	};
}
