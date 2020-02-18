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
	
	public Response<ShopModel> createShop(ShopModel shop, String oauthId, String accessToken) {
		return shopDao.createShop(shop, oauthId, accessToken);
	}

	public Response<List<ShopModel>> getAllShops(String oauthId, String accessToken) {
		return shopDao.getAllShops(oauthId,accessToken);
	}
	
	public Response<ShopModel> getShopById(Integer shopId,String oauthIdRh, String accessToken) {
		return shopDao.getShopById(shopId,oauthIdRh,accessToken);
    }
	
	public Response<List<ShopModel>> getShopByCollegeId(Integer collegeId,String oauthIdRh, String accessToken) {
		return shopDao.getShopsByCollegeId(collegeId, oauthIdRh, accessToken);
    }

	public Response<ShopModel> updateShopById(ShopModel shop,String oauthId, String accessToken) {
		return shopDao.updateShopById(shop,oauthId,accessToken);
	}
	
	public Response<ShopModel> deleteShopById(Integer shopId,String oauthIdRh, String accessToken) {
		return shopDao.deleteShopById(shopId,oauthIdRh,accessToken);
	}
}
