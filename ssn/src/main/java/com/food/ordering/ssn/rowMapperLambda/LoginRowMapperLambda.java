package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.UserModel;

public class LoginRowMapperLambda {
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
}
