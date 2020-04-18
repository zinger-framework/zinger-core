package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.RatingColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.RatingQuery;
import com.food.ordering.zinger.dao.interfaces.*;
import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.rowMapperLambda.RatingRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class RatingDaoImpl implements RatingDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    InterceptorDaoImpl interceptorDaoImpl;

    @Override
    public Response<RatingModel> getRatingByShopId(ShopModel shopModel) {
        Response<RatingModel> response = new Response<>();
        RatingModel ratingModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(RatingColumn.shopId, shopModel.getId());

            try {
                ratingModel = namedParameterJdbcTemplate.queryForObject(RatingQuery.getRatingByShopId, parameters, RatingRowMapperLambda.ratingRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (ratingModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                ratingModel.setShopModel(shopModel);
                response.setData(ratingModel);
            }
        }
        return response;
    }

    @Override
    public void updateShopRating(Integer shopId, Double rating) {
        try {
            ShopModel shopModel = new ShopModel();
            shopModel.setId(shopId);
            Response<RatingModel> ratingModelResponse = getRatingByShopId(shopModel);

            if (ratingModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                RatingModel ratingModel = ratingModelResponse.getData();
                Double oldRating = ratingModel.getRating() * ratingModel.getUserCount();
                Double newRating = (oldRating + rating) / (ratingModel.getUserCount() + 1);

                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(RatingColumn.rating, newRating)
                        .addValue(RatingColumn.userCount, ratingModel.getUserCount() + 1)
                        .addValue(RatingColumn.shopId, shopId);

                namedParameterJdbcTemplate.update(RatingQuery.updateRating, parameter);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
