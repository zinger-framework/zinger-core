package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.*;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.UserShopColumn.*;

public class UserShopRowMapperLambda {
	public static final RowMapper<UserShopModel> userShopRowMapperLambda = (rs, rownum) -> {
		UserShopModel userShopModel = new UserShopModel();

		UserModel userModel = new UserModel();
		userModel.setOauthId(rs.getString(oauthId));
		userShopModel.setUserModel(userModel);

		ShopModel shopModel = new ShopModel();
		shopModel.setId(rs.getInt(shopId));
		userShopModel.setShopModel(shopModel);

		return userShopModel;
	};
}
