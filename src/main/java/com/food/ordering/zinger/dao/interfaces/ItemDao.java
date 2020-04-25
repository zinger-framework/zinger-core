package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.ItemColumn;
import com.food.ordering.zinger.constant.Column.OrderItemColumn;
import com.food.ordering.zinger.constant.Column.ShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.ItemQuery;
import com.food.ordering.zinger.constant.Query.OrderItemQuery;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.ItemLogModel;
import com.food.ordering.zinger.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.OrderItemRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;

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
