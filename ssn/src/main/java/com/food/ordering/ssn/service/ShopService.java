package com.food.ordering.ssn.service;

import com.food.ordering.ssn.dao.ItemDao;
import com.food.ordering.ssn.dao.ShopDao;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    ShopDao shopDao;

    @Autowired
    ItemDao itemDao;

    public Response<List<ShopModel>> getShopByCollegeId(Integer collegeId, String oauthId, String mobile, String role) {
        return shopDao.getShopsByCollegeId(collegeId, oauthId, mobile, role);
    }
}
