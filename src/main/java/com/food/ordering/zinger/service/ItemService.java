package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.ItemDao;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;

    public Response<List<ItemModel>> getItemsByShopId(ShopModel shopModel, String oauthId, String mobile, String role) {
        return itemDao.getItemsByShopId(shopModel, oauthId, mobile, role);
    }

    public Response<List<ItemModel>> getItemsByName(Integer collegeId, String itemName, String oauthId, String mobile, String role) {
        return itemDao.getItemsByName(collegeId, itemName, oauthId, mobile, role);
    }
}
