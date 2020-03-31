package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.ShopDao;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    ShopDao shopDao;

    public Response<String> insertShop(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return shopDao.insertShop(configurationModel, requestHeaderModel);
    }

    public Response<List<ShopConfigurationModel>> getShopByCollegeId(Integer collegeId, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return shopDao.getShopsByCollegeId(collegeId, requestHeaderModel);
    }

    public Response<String> updateShopConfiguration(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return shopDao.updateShopConfigurationModel(configurationModel, requestHeaderModel);
    }
}
