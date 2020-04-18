package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.dao.impl.ShopDaoImpl;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShopService {

    Response<String> insertShop(ConfigurationModel configurationModel, String oauthId, Integer id, String role);

    Response<List<ShopConfigurationModel>> getShopByPlaceId(Integer placeId, String oauthId, Integer id, String role);

    Response<String> updateShopConfiguration(ConfigurationModel configurationModel, String oauthId, Integer id, String role);
}
