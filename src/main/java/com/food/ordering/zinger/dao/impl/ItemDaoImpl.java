package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.ItemColumn;
import com.food.ordering.zinger.constant.Column.OrderItemColumn;
import com.food.ordering.zinger.constant.Column.ShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.ItemQuery;
import com.food.ordering.zinger.constant.Query.OrderItemQuery;
import com.food.ordering.zinger.dao.interfaces.ItemDao;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.OrderItemModel;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.OrderItemRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;
import static com.food.ordering.zinger.constant.Sql.PERCENT;

/**
 * ItemDao is responsible for CRUD operations in
 * Item table in MySQL.
 *
 * @implNote Request Header (RH) parameter is sent in all endpoints
 * to avoid unauthorized access to our service.
 * @implNote Please check the Shop and Order table for better understanding.
 * @implNote All endpoint services are audited for both success and error responses
 * using "AuditLogDao".
 * <p>
 * Endpoints starting with "/menu" invoked here.
 */
@Repository
@Transactional
public class ItemDaoImpl implements ItemDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Inserts the list of item details for the shop.
     * Authorized by SHOP_OWNER and workers(SELLER/DELIVERY) only.
     *
     * @param itemModelList List<ItemModel>
     * @return success response if both the insertion is successful.
     */
    @Override
    public Response<String> insertItem(List<ItemModel> itemModelList) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource();
            for (int i = 0; i < itemModelList.size(); i++) {
                ItemModel itemModel = itemModelList.get(i);
                parameter.addValue(ItemColumn.name + i, itemModel.getName())
                        .addValue(ItemColumn.price + i, itemModel.getPrice())
                        .addValue(ItemColumn.photoUrl + i, itemModel.getPhotoUrl())
                        .addValue(ItemColumn.category + i, itemModel.getCategory())
                        .addValue(ItemColumn.shopId + i, itemModel.getShopModel().getId())
                        .addValue(ItemColumn.isVeg + i, itemModel.getIsVeg());
            }

            int responseValue = namedParameterJdbcTemplate.update(ItemQuery.getInsertItem(itemModelList), parameter);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            } else {
                response.setCode(IDNU1201);
                response.setMessage(ItemDetailNotUpdated);
            }
        } catch (Exception e) {
            response.setCode(CE1202);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Gets list of items by shop id.
     *
     * @param shopId Integer
     * @return the details of the list of items for the given shop id.
     */
    @Override
    public Response<List<ItemModel>> getItemsByShopId(Integer shopId) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> list = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.shopId, shopId);

            list = namedParameterJdbcTemplate.query(ItemQuery.getItemsByShopId, parameters, ItemRowMapperLambda.itemDetailRowMapperLambda);
        } catch (Exception e) {
            response.setCode(CE1204);
            response.setMessage(ItemDetailNotAvailable);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (list != null) {
                response.setCode(list.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
                response.prioritySet(Priority.LOW);
            }
        }

        return response;
    }

    /**
     * Gets list of items matching the item name, along with
     * the shop details, located in the given place.
     *
     * @param placeId  Integer
     * @param itemName String
     * @return the details of the list of items, if the match is successful.
     * @implNote Used mainly for search/filter the items by name.
     */
    @Override
    public Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> items = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.name, PERCENT + itemName + PERCENT)
                    .addValue(ShopColumn.placeId, placeId);

            items = namedParameterJdbcTemplate.query(ItemQuery.getItemsByName, parameters, ItemRowMapperLambda.itemRowMapperLambda);
        } catch (Exception e) {
            response.setCode(IDNA1205);
            response.setMessage(ItemDetailNotAvailable);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (items != null) {
                response.setCode(items.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(items);
                response.prioritySet(Priority.LOW);
            }
        }

        return response;
    }

    /**
     * Gets item by id.
     *
     * @param id Integer
     * @return the details of the item.
     */
    public Response<ItemModel> getItemById(Integer id) {
        //TODO: May not be needed
        ItemModel item = null;
        Response<ItemModel> response = new Response<>();
        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.id, id);
            try {
                item = namedParameterJdbcTemplate.queryForObject(ItemQuery.getItemById, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (item != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                //Response<ShopModel> shopModelResponse = shopDaoImpl.getShopById(item.getShopModel().getId());
                //item.setShopModel(shopModelResponse.getData());
                response.setData(item);
            }
        }
        return response;
    }

    /**
     * Gets list of items for the given order id.
     *
     * @param orderModel OrderModel
     * @return the details of the list of items.
     */
    public Response<List<OrderItemModel>> getItemsByOrderId(OrderModel orderModel) {
        //TODO: May not be needed
        Response<List<OrderItemModel>> response = new Response<>();
        List<OrderItemModel> orderItemModelList = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(OrderItemColumn.orderId, orderModel.getId());

            try {
                orderItemModelList = namedParameterJdbcTemplate.query(OrderItemQuery.getItemByOrderId, parameters, OrderItemRowMapperLambda.orderItemRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                if (orderItemModelList != null && orderItemModelList.size() > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    orderItemModelList.stream().forEach(s -> {
                        s.setOrderModel(null);
                        Response<ItemModel> itemModelResponse = getItemById(s.getItemModel().getId());
                        if (itemModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            itemModelResponse.getData().setShopModel(null);
                            s.setItemModel(itemModelResponse.getData());
                        } else {
                            s.setItemModel(null);
                        }
                    });
                    response.setData(orderItemModelList);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Updates the item details for the given item.
     * Authorized by SHOP_OWNER and workers(SELLER/DELIVERY) only.
     *
     * @param itemModelList List<ItemModel>
     * @return success response if the update is successful.
     */
    @Override
    public Response<String> updateItem(List<ItemModel> itemModelList) {
        Response<String> response = new Response<>();

        for (int i = 0; i < itemModelList.size(); i++) {
            ItemModel itemModel = itemModelList.get(i);
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.name, itemModel.getName())
                    .addValue(ItemColumn.price, itemModel.getPrice())
                    .addValue(ItemColumn.photoUrl, itemModel.getPhotoUrl())
                    .addValue(ItemColumn.category, itemModel.getCategory())
                    .addValue(ItemColumn.isVeg, itemModel.getIsVeg())
                    .addValue(ItemColumn.isAvailable, itemModel.getIsAvailable())
                    .addValue(ItemColumn.id, itemModel.getId());

            namedParameterJdbcTemplate.update(ItemQuery.updateItem, parameters);
        }
        response.setCode(CodeSuccess);
        response.setMessage(Success);
        response.setData(Success);

        return response;
    }

    /**
     * Deletes the item by id
     * Authorized by SHOP_OWNER and workers(SELLER/DELIVERY) only.
     *
     * @param itemId Integer
     * @return success response if the delete is successful.
     */
    @Override
    public Response<String> deleteItemById(Integer itemId) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.id, itemId);

            int responseValue = namedParameterJdbcTemplate.update(ItemQuery.deleteItem, parameters);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            } else {
                response.setCode(IDNU1210);
                response.setMessage(ItemDetailNotUpdated);
            }
        } catch (Exception e) {
            response.setCode(CE1209);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }
}
