package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.impl.ShopDaoImpl;
import com.food.ordering.zinger.dao.interfaces.ShopDao;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.service.interfaces.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    ShopDao shopDao;

    @Override
    public Response<String> insertShop(ConfigurationModel configurationModel) {
        return shopDao.insertShop(configurationModel);
    }

    @Override
    public Response<List<ShopConfigurationModel>> getShopByPlaceId(Integer placeId) {
        return shopDao.getShopsByPlaceId(placeId);
    }

    @Override
    public Response<String> updateShopConfiguration(ConfigurationModel configurationModel) {
        return shopDao.updateShopConfigurationModel(configurationModel);
    }
}
