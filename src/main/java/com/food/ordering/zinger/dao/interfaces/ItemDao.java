package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.OrderItemModel;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.Response;

import java.util.List;

public interface ItemDao {
    Response<String> insertItem(List<ItemModel> itemModelList);

    Response<List<ItemModel>> getItemsByShopId(Integer shopId);

    Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName);

    Response<ItemModel> getItemById(Integer id);

    Response<List<OrderItemModel>> getItemsByOrderId(OrderModel orderModel);

    Response<String> updateItemById(ItemModel itemModel);

    Response<String> deleteItemById(Integer itemId);

    Response<String> unDeleteItemById(Integer itemId);
}
