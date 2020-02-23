package com.food.ordering.ssn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.food.ordering.ssn.dao.ShopDao;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.utils.Response;

@Service
public class ShopService {

	@Autowired
	ShopDao shopDao;
	
	public Response<List<ShopModel>> getShopByCollegeId(Integer collegeId,String oauthIdRh) {
		return shopDao.getShopsByCollegeId(collegeId, oauthIdRh);
    }

}
