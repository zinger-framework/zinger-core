package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.*;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.RatingColumn.*;

public class RatingRowMapperLambda {
	public static final RowMapper<RatingModel> ratingRowMapperLambda = (rs, rownum) -> {
		RatingModel ratingModel = new RatingModel();

		ShopModel shopModel = new ShopModel();
		shopModel.setId(rs.getInt(shopId));
		ratingModel.setShopModel(shopModel);

		ratingModel.setRating(rs.getDouble(rating));
		ratingModel.setUserCount(rs.getInt(userCount));
		return ratingModel;
	};
}
