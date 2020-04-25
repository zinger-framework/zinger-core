package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopModel;

public interface ConfigurationDao {
    Response<String> insertConfiguration(ConfigurationModel configurationModel);

    Response<ConfigurationModel> getConfigurationByShopId(ShopModel shopModel);

    Response<String> updateConfigurationModel(ConfigurationModel configurationModel);
}
