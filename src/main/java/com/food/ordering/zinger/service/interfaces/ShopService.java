package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopConfigurationModel;

import java.util.List;

public interface ShopService {

    Response<String> insertShop(ConfigurationModel configurationModel);

    Response<ShopConfigurationModel> getShopById(Integer placeId);

    Response<List<ShopConfigurationModel>> getShopByPlaceId(Integer placeId);

    Response<String> updateShopConfiguration(ConfigurationModel configurationModel);
}
