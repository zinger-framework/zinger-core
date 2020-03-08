package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.ShopDao;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    ShopDao shopDao;

    public Response<List<ShopModel>> getShopByCollegeId(CollegeModel collegeModel, String oauthId, String mobile, String role) {
        return shopDao.getShopsByCollegeId(collegeModel, oauthId, mobile, role);
    }
}
