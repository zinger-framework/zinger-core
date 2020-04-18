package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.Response;

import java.util.List;

public interface ItemService {
    Response<String> insertItem(List<ItemModel> itemModelList, String oauthId, Integer id, String role);

    Response<List<ItemModel>> getItemsByShopId(Integer shopId, String oauthId, Integer id, String role);

    Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName, String oauthId, Integer id, String role);

    Response<String> updateItemById(ItemModel itemModel, String oauthId, Integer id, String role);

    Response<String> deleteItemById(Integer itemId, String oauthId, Integer id, String role);

    Response<String> unDeleteItemById(Integer itemId, String oauthId, Integer id, String role);
}
