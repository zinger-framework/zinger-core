package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.ConfigurationDao;
import com.food.ordering.zinger.dao.ShopDao;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.rowMapperLambda.ConfigurationRowMapperLambda;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    ShopDao shopDao;

    public Response<String> insertShop(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return shopDao.insertShop(configurationModel, responseHeader);
    }

    public Response<List<ShopConfigurationModel>> getShopByCollegeId(Integer collegeId, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return shopDao.getShopsByCollegeId(collegeId, responseHeader);
    }

    public Response<String> updateShopConfiguration(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return shopDao.updateShopConfigurationModel(configurationModel, responseHeader);
    }
}
