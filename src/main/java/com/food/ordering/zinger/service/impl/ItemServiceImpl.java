package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.ItemDao;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.service.interfaces.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemDao itemDao;

    @Override
    public Response<String> insertItem(List<ItemModel> itemModelList, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        if (itemModelList != null && itemModelList.size() > 0)
            return itemDao.insertItem(itemModelList, requestHeaderModel);
        return new Response<>();
    }

    @Override
    public Response<List<ItemModel>> getItemsByShopId(Integer shopId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return itemDao.getItemsByShopId(shopId, requestHeaderModel);
    }

    @Override
    public Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return itemDao.getItemsByName(placeId, itemName, requestHeaderModel);
    }

    @Override
    public Response<String> updateItemById(ItemModel itemModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return itemDao.updateItemById(itemModel, requestHeaderModel);
    }

    @Override
    public Response<String> deleteItemById(Integer itemId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return itemDao.deleteItemById(itemId, requestHeaderModel);
    }

    @Override
    public Response<String> unDeleteItemById(Integer itemId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return itemDao.unDeleteItemById(itemId, requestHeaderModel);
    }
}
