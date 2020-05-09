package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.ShopDao;
import com.food.ordering.zinger.exception.GenericException;
import com.food.ordering.zinger.model.ConfigurationModel;
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
        Response<String> response = new Response<>();
        try {
            response = shopDao.insertShop(configurationModel);
        } catch (GenericException e) {
            response = e.getResponse();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ShopConfigurationModel> getShopById(Integer shopId) {
        Response<ShopConfigurationModel> response = shopDao.getShopById(shopId);
        return response;
    }

    @Override
    public Response<List<ShopConfigurationModel>> getShopByPlaceId(Integer placeId) {
        Response<List<ShopConfigurationModel>> response = shopDao.getShopsByPlaceId(placeId);
        return response;
    }

    @Override
    public Response<String> updateShopConfiguration(ConfigurationModel configurationModel) {
        Response<String> response = shopDao.updateShopConfigurationModel(configurationModel);
        return response;
    }

    @Override
    public Response<String> deleteShopById(Integer shopId) {
        Response<String> response = shopDao.deleteShopById(shopId);
        return response;
    }
}
