package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.ConfigurationColumn;
import com.food.ordering.zinger.column.RatingColumn;
import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.query.RatingQuery;
import com.food.ordering.zinger.rowMapperLambda.RatingRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class RatingDao {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    public Response<RatingModel> getRatingByShopId(ShopModel shopModel) {
        Response<RatingModel> response = new Response<>();
        RatingModel ratingModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(RatingColumn.shopId, shopModel.getId());

            try {
                ratingModel = namedParameterJdbcTemplate.queryForObject(RatingQuery.getRatingByShopId, parameters, RatingRowMapperLambda.ratingRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void updateShopRating(Integer shopId, Double rating) {
        try {
            ShopModel shopModel = new ShopModel();
            shopModel.setId(shopId);
            Response<RatingModel> ratingModelResponse = getRatingByShopId(shopModel);

            if (ratingModelResponse.getCode().equals(ErrorLog.CodeSuccess) && ratingModelResponse.getMessage().equals(ErrorLog.Success)) {
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
            e.printStackTrace();
        }
    }
}
