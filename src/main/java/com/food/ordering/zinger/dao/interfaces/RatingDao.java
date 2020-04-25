package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopModel;

public interface RatingDao {
    Response<RatingModel> getRatingByShopId(ShopModel shopModel);

    void updateShopRating(Integer shopId, Double rating);
}
