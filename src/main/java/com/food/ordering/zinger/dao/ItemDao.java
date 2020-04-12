package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.Column.ItemColumn;
import com.food.ordering.zinger.constant.Column.OrderItemColumn;
import com.food.ordering.zinger.constant.Column.ShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.ItemLogModel;
import com.food.ordering.zinger.constant.Query.ItemQuery;
import com.food.ordering.zinger.constant.Query.OrderItemQuery;
import com.food.ordering.zinger.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.OrderItemRowMapperLambda;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;

@Repository
public class ItemDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    ShopDao shopDao;

    @Autowired
    AuditLogDao auditLogDao;

    public Response<String> insertItem(ItemModel itemModel, RequestHeaderModel requestHeaderModel) {

        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1009);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1010);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ItemColumn.name, itemModel.getName())
                        .addValue(ItemColumn.price, itemModel.getPrice())
                        .addValue(ItemColumn.photoUrl, itemModel.getPhotoUrl())
                        .addValue(ItemColumn.category, itemModel.getCategory())
                        .addValue(ItemColumn.shopId, itemModel.getShopModel().getId())
                        .addValue(ItemColumn.isVeg, itemModel.getIsVeg());

                int responseValue = namedParameterJdbcTemplate.update(ItemQuery.insertItem, parameters);

                if (responseValue > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(IDNU1201);
                    response.setMessage(ItemDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(CE1202);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }


        auditLogDao.insertItemLog(new ItemLogModel(response, requestHeaderModel.getMobile(), itemModel.getId(), itemModel.toString(), priority));
        return response;
    }

    public Response<List<ItemModel>> getItemsByShopId(Integer shopId, RequestHeaderModel requestHeaderModel) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> list = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1011);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ItemColumn.shopId, shopId);
                try {
                    list = namedParameterJdbcTemplate.query(ItemQuery.getItemsByShopId, parameters, ItemRowMapperLambda.itemRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(IDNA1203);
                    response.setMessage(ItemDetailNotAvailable);
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            response.setCode(CE1204);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                for (int i = 0; i < list.size(); i++)
                    list.get(i).setShopModel(null);
                response.setData(list);
                priority = Priority.LOW;
            }
        }

        auditLogDao.insertItemLog(new ItemLogModel(response, requestHeaderModel.getMobile(), shopId, shopId.toString(), priority));
        return response;
    }

    public Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName, RequestHeaderModel requestHeaderModel) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> items = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1012);
                response.setMessage(ErrorLog.InvalidHeader);
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ItemColumn.name, "%" + itemName + "%")
                        .addValue(ShopColumn.placeId, placeId);

                try {
                    items = namedParameterJdbcTemplate.query(ItemQuery.getItemsByName, parameters, ItemRowMapperLambda.itemRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(IDNA1205);
                    response.setMessage(ItemDetailNotAvailable);
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            response.setCode(CE1206);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (items != null && !items.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                for (int i = 0; i < items.size(); i++) {
                    Response<ShopModel> shopModelResponse = shopDao.getShopById(items.get(i).getShopModel().getId());
                    shopModelResponse.getData().setPlaceModel(null);
                    items.get(i).setShopModel(shopModelResponse.getData());
                }
                response.setData(items);
                priority = Priority.LOW;
            }
        }

        auditLogDao.insertItemLog(new ItemLogModel(response, requestHeaderModel.getMobile(), null, itemName, priority));
        return response;
    }

    public Response<ItemModel> getItemById(Integer id) {
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
                Response<ShopModel> shopModelResponse = shopDao.getShopById(item.getShopModel().getId());
                item.setShopModel(shopModelResponse.getData());
                response.setData(item);
            }
        }
        return response;
    }

    public Response<List<OrderItemModel>> getItemsByOrderId(OrderModel orderModel) {

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
                        if (itemModelResponse.getCode().equals(ErrorLog.CodeSuccess) && itemModelResponse.getMessage().equals(ErrorLog.Success)) {
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

    public Response<String> updateItemById(ItemModel itemModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1013);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1014);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ItemColumn.name, itemModel.getName())
                        .addValue(ItemColumn.price, itemModel.getPrice())
                        .addValue(ItemColumn.photoUrl, itemModel.getPhotoUrl())
                        .addValue(ItemColumn.category, itemModel.getCategory())
                        .addValue(ItemColumn.isVeg, itemModel.getIsVeg())
                        .addValue(ItemColumn.isAvailable, itemModel.getIsAvailable())
                        .addValue(ItemColumn.id, itemModel.getId());

                int responseValue = namedParameterJdbcTemplate.update(ItemQuery.updateItem, parameters);
                if (responseValue > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(IDNU1207);
                    response.setMessage(ItemDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(CE1208);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertItemLog(new ItemLogModel(response, requestHeaderModel.getMobile(), itemModel.getId(), itemModel.toString(), priority));
        return response;
    }

    public Response<String> deleteItemById(Integer itemId, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1015);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1016);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ItemColumn.id, itemId);

                int responseValue = namedParameterJdbcTemplate.update(ItemQuery.deleteItem, parameters);
                if (responseValue > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(IDNU1210);
                    response.setMessage(ItemDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(CE1209);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertItemLog(new ItemLogModel(response, requestHeaderModel.getMobile(), itemId, null, priority));
        return response;
    }

    public Response<String> unDeleteItemById(Integer itemId, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1017);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1018);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ItemColumn.id, itemId);

                int responseValue = namedParameterJdbcTemplate.update(ItemQuery.unDeleteItem, parameters);
                if (responseValue > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(IDNU1211);
                    response.setMessage(ItemDetailNotUpdated);
                }
            }

        } catch (Exception e) {
            response.setCode(CE1212);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertItemLog(new ItemLogModel(response, requestHeaderModel.getMobile(), itemId, null, priority));
        return response;
    }
}
