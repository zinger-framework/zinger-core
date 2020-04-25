package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.ItemDao;
import com.food.ordering.zinger.model.ItemModel;
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
    public Response<String> insertItem(List<ItemModel> itemModelList) {
        if (itemModelList != null && itemModelList.size() > 0)
            return itemDao.insertItem(itemModelList);
        return new Response<>();
    }

    @Override
    public Response<List<ItemModel>> getItemsByShopId(Integer shopId) {
        return itemDao.getItemsByShopId(shopId);
    }

    @Override
    public Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName) {
        return itemDao.getItemsByName(placeId, itemName);
    }

    @Override
    public Response<String> updateItemById(ItemModel itemModel) {
        return itemDao.updateItemById(itemModel);
    }

    @Override
    public Response<String> deleteItemById(Integer itemId) {
        return itemDao.deleteItemById(itemId);
    }

    @Override
    public Response<String> unDeleteItemById(Integer itemId) {
        return itemDao.unDeleteItemById(itemId);
    }
}
