package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RatingDao {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    //TODO: Finish rating
    public Response<RatingModel> getRatingByShopId(ShopModel shopModel){
        Response<RatingModel> response = new Response<>();
        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);

        RatingModel ratingModel = new RatingModel();
        ratingModel.setRating(4.1);
        ratingModel.setUserCount(23);
        ratingModel.setShopModel(shopModel);
        response.setData(ratingModel);

        return response;
    }
}
