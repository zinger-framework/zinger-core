package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.model.ShopModel;

import java.util.List;

public interface ShopDao {
    Response<String> insertShop(ConfigurationModel configurationModel);

    Response<List<ShopConfigurationModel>> getShopsByPlaceId(Integer placeId);

    Response<ShopConfigurationModel> getShopConfigurationById(Integer shopId);

    Response<ShopModel> getShopById(Integer shopId);

    Response<String> updateShopConfigurationModel(ConfigurationModel configurationModel);
}
