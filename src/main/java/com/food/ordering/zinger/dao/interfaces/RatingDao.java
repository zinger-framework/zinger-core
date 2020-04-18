package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.RatingColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.RatingQuery;
import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.rowMapperLambda.RatingRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

public interface RatingDao {
    Response<RatingModel> getRatingByShopId(ShopModel shopModel);

    void updateShopRating(Integer shopId, Double rating);
}
