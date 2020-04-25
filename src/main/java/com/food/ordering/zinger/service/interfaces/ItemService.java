package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.Response;

import java.util.List;

public interface ItemService {
    Response<String> insertItem(List<ItemModel> itemModelList);

    Response<List<ItemModel>> getItemsByShopId(Integer shopId);

    Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName);

    Response<String> updateItemById(ItemModel itemModel);

    Response<String> deleteItemById(Integer itemId);

    Response<String> unDeleteItemById(Integer itemId);
}
