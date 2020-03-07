package com.food.ordering.ssn.service;

import com.food.ordering.ssn.dao.ItemDao;
import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;

    public Response<ItemModel> getItemById(Integer itemId, String oauthId, String mobile) {
        return itemDao.getItemById(itemId, oauthId, mobile);
    }

    public Response<List<ItemModel>> getItemsByShopId(Integer shopId, String oauthId, String mobile) {
        return itemDao.getItemsByShopId(shopId, oauthId, mobile);
    }

    public Response<List<ItemModel>> getItemsByName(Integer collegeId, String itemName, String oauthId, String mobile) {
        return itemDao.getItemsByName(collegeId, itemName, oauthId, mobile);
    }
}
