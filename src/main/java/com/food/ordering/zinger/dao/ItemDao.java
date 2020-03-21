package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.ItemColumn;
import com.food.ordering.zinger.column.OrderItemColumn;
import com.food.ordering.zinger.column.ShopColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.logger.ItemLogModel;
import com.food.ordering.zinger.query.ItemQuery;
import com.food.ordering.zinger.query.OrderItemQuery;
import com.food.ordering.zinger.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.OrderItemRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        ItemLogModel itemLogModel = new ItemLogModel();
        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setMobile(requestHeaderModel.getMobile());

        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(itemModel.toString());

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1009);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1010);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

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

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(itemModel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<List<ItemModel>> getItemsByShopId(Integer shopId, RequestHeaderModel requestHeaderModel) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> list = null;
        ItemLogModel itemLogModel = new ItemLogModel();
        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setMobile(requestHeaderModel.getMobile());

        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(requestHeaderModel.getMobile());


        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1011);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(requestHeaderModel.getMobile());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.shopId, shopId);

            try {
                list = namedParameterJdbcTemplate.query(ItemQuery.getItemsByShopId, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                for (int i = 0; i < list.size(); i++)
                    list.get(i).setShopModel(null);

                response.setData(list);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(shopId.toString());

            }
        }
        return response;
    }

    public Response<List<ItemModel>> getItemsByName(Integer collegeId, String itemName, RequestHeaderModel requestHeaderModel) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> items = null;
        ItemLogModel itemLogModel = new ItemLogModel();

        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(collegeId.toString());

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1012);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemLogModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.name, "%" + itemName + "%")
                    .addValue(ShopColumn.collegeId, collegeId);

            try {
                items = namedParameterJdbcTemplate.query(ItemQuery.getItemsByName, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (items != null && !items.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                for (int i = 0; i < items.size(); i++) {
                    Response<ShopModel> shopModelResponse = shopDao.getShopById(items.get(i).getShopModel().getId());
                    items.get(i).setShopModel(shopModelResponse.getData());
                }
                response.setData(items);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(collegeId.toString());
            }
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<ItemModel> getItemById(Integer id) {
        ItemModel item = null;
        Response<ItemModel> response = new Response<>();
        ItemLogModel itemLogModel = new ItemLogModel();

        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(id.toString());
        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.id, id);

            try {
                item = namedParameterJdbcTemplate.queryForObject(ItemQuery.getItemById, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (item != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                Response<ShopModel> shopModelResponse = shopDao.getShopById(item.getId());
                item.setShopModel(shopModelResponse.getData());
                response.setData(item);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(id.toString());
            }
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<List<OrderItemModel>> getItemsByOrderId(OrderModel orderModel) {

        Response<List<OrderItemModel>> response = new Response<>();
        List<OrderItemModel> orderItemModelList = null;
        ItemLogModel itemLogModel = new ItemLogModel();
        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setMobile(itemLogModel.getMobile());

        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(orderModel.toString());

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(OrderItemColumn.orderId, orderModel.getId());

            try {
                orderItemModelList = namedParameterJdbcTemplate.query(OrderItemQuery.getItemByOrderId, parameters, OrderItemRowMapperLambda.orderItemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (orderItemModelList != null && orderItemModelList.size() > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    orderItemModelList.stream().forEach(s -> {
                        s.setOrderModel(null);
                        Response<ItemModel> itemModelResponse = getItemById(s.getItemModel().getId());
                        if (itemModelResponse.getCode().equals(ErrorLog.CodeSuccess) && itemModelResponse.getMessage().equals(ErrorLog.Success)) {
                            s.setItemModel(itemModelResponse.getData());
                        } else {
                            s.setItemModel(null);
                        }
                    });
                    response.setData(orderItemModelList);

                    itemLogModel.setErrorCode(response.getCode());
                    itemLogModel.setMessage(response.getMessage());
                    itemLogModel.setPriority(Priority.LOW);
                    itemLogModel.setUpdatedValue(orderModel.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> updateItemById(ItemModel itemModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        ItemLogModel itemLogModel = new ItemLogModel();
        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setMobile(requestHeaderModel.getMobile());

        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(itemLogModel.toString());
        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1013);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemLogModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1014);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

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

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(itemModel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> deleteItemById(ItemModel itemModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        ItemLogModel itemLogModel = new ItemLogModel();
        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setMobile(requestHeaderModel.getMobile());

        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(itemModel.toString());

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1015);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1016);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemLogModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.id, itemModel.getId());

            int responseValue = namedParameterJdbcTemplate.update(ItemQuery.deleteItem, parameters);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(itemLogModel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> unDeleteItemById(ItemModel itemModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        ItemLogModel itemLogModel = new ItemLogModel();
        itemLogModel.setId(itemLogModel.getId());
        itemLogModel.setMobile(requestHeaderModel.getMobile());

        itemLogModel.setErrorCode(response.getCode());
        itemLogModel.setMessage(response.getMessage());
        itemLogModel.setUpdatedValue(itemLogModel.toString());

        try {
            if (requestHeaderModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response.setCode(ErrorLog.IH1017);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1018);
                response.setMessage(ErrorLog.InvalidHeader);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.HIGH);
                itemLogModel.setUpdatedValue(itemLogModel.toString());

                try {
                    auditLogDao.insertItemLog(itemLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.id, itemModel.getId());

            int responseValue = namedParameterJdbcTemplate.update(ItemQuery.unDeleteItem, parameters);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);

                itemLogModel.setErrorCode(response.getCode());
                itemLogModel.setMessage(response.getMessage());
                itemLogModel.setPriority(Priority.LOW);
                itemLogModel.setUpdatedValue(itemModel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertItemLog(itemLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
