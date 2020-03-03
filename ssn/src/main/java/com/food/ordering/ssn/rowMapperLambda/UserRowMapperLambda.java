package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.enums.*;
import static com.food.ordering.ssn.column.UserColumn.*;
import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.*;

public class UserRowMapperLambda {
	public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
		UserModel userModel = new UserModel();
		userModel.setMobile(rs.getString(mobile));
		userModel.setName(rs.getString(name));
		userModel.setEmail(rs.getString(email));
		userModel.setOauthId(rs.getString(oauthId));
		userModel.setRole(UserRole.valueOf(rs.getString(role)));
		userModel.setIsDelete(rs.getInt(isDelete));
		return userModel;
	};
}
