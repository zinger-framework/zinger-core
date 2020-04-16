package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.ItemDao;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemDao itemDao;


    public Response<String> insertItem(List<ItemModel> itemModelList, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        if(itemModelList != null && itemModelList.size()>0)
            return itemDao.insertItem(itemModelList, requestHeaderModel);
        return new Response<>();
    }

    public Response<List<ItemModel>> getItemsByShopId(Integer shopId, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return itemDao.getItemsByShopId(shopId, requestHeaderModel);
    }

    public Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return itemDao.getItemsByName(placeId, itemName, requestHeaderModel);
    }

    public Response<String> updateItemById(ItemModel itemModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return itemDao.updateItemById(itemModel, requestHeaderModel);
    }

    public Response<String> deleteItemById(Integer itemId, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return itemDao.deleteItemById(itemId, requestHeaderModel);
    }

    public Response<String> unDeleteItemById(Integer itemId, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return itemDao.unDeleteItemById(itemId, requestHeaderModel);
    }

}
