package com.food.ordering.ssn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.food.ordering.ssn.dao.ItemDao;
import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.utils.Response;

@Service
public class ItemService {
		
	@Autowired
	ItemDao itemDao;
	
	public Response<ItemModel> getItemById(Integer itemId,String oauthId) {
		return itemDao.getItemById(itemId,oauthId);
    }
	
	public Response<List<ItemModel>> getItemsByShopId(Integer shopId,String oauthId) {
		return itemDao.getItemsByShopId(shopId, oauthId);
    }
	
	public Response<List<ItemModel>> getItemsByQuery(String oauthId, Integer shopId, String query){
		return itemDao.getItemsByQuery(oauthId, shopId, query);
	}
	
	
}
