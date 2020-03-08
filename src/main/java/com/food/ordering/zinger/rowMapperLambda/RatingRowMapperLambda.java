package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.ShopModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.RatingColumn.*;

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
