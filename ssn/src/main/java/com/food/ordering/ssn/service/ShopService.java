package com.food.ordering.ssn.service;

import java.util.List;

import com.food.ordering.ssn.dao.ItemDao;
import com.food.ordering.ssn.model.ItemModel;
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

	@Autowired
	ItemDao itemDao;
	
	public Response<List<ShopModel>> getShopByCollegeId(Integer collegeId,String oauthIdRh,String mobile) {
		return shopDao.getShopsByCollegeId(collegeId,oauthIdRh,mobile);
    }

    public Response<List<ItemModel>> getMenuByShopId(Integer shopId,String oauthIdRh,String mobile) {
		return itemDao.getItemsByShopId(shopId,oauthIdRh,mobile);
	}

	public Response<List<ItemModel>> getItemsByName(Integer collegeId,String itemName,String oauthIdRh,String mobile) {
		return itemDao.getItemsByName(collegeId,itemName,oauthIdRh,mobile);
	}
	

}
