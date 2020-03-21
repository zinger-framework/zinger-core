package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.ConfigurationDao;
import com.food.ordering.zinger.dao.ShopDao;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.rowMapperLambda.ConfigurationRowMapperLambda;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    ShopDao shopDao;

    public Response<String> insertShop(ConfigurationModel configurationModel,String oauthId, String mobile, String role) {
        return shopDao.insertShop(configurationModel,oauthId,mobile,role);
    }

    public Response<List<ShopConfigurationModel>> getShopByCollegeId(Integer collegeId, String oauthId, String mobile, String role) {
        return shopDao.getShopsByCollegeId(collegeId, oauthId, mobile, role);
    }

    public Response<String> updateShopConfiguration(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {
        return shopDao.updateShopConfigurationModel(configurationModel, oauthId, mobile, role);
    }
}
