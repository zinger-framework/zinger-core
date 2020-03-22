package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.OrderColumn;
import com.food.ordering.zinger.column.OrderItemColumn;
import com.food.ordering.zinger.enums.OrderStatus;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.OrderLogModel;
import com.food.ordering.zinger.query.OrderItemQuery;
import com.food.ordering.zinger.query.OrderQuery;
import com.food.ordering.zinger.rowMapperLambda.OrderRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.PaytmResponseLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.food.ordering.zinger.column.OrderColumn.*;

@Repository
public class OrderDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    TransactionDao transactionDao;

    @Autowired
    ItemDao itemDao;

    @Autowired
    ShopDao shopDao;

    @Autowired
    RatingDao ratingDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AuditLogDao auditLogDao;

    @Autowired
    ConfigurationDao configurationDao;

    public Response<String> insertOrder(OrderItemListModel orderItemListModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1003);
                response.setData(ErrorLog.InvalidHeader);
            } else {
                OrderModel order = orderItemListModel.getOrderModel();
                TransactionModel transaction = order.getTransactionModel();

                Response<String> transactionResult = transactionDao.insertTransactionDetails(transaction);
                if (!transactionResult.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.TDNU1264);
                    response.setData(ErrorLog.TransactionDetailNotUpdated);
                } else if (transaction.getResponseCode().equals(PaytmResponseLog.TxnSuccessfulCode) && transaction.getResponseMessage().equals(PaytmResponseLog.TxnSuccessful) && checkOrderStatusValidity(null, order.getOrderStatus())) {
                    MapSqlParameterSource parameter = new MapSqlParameterSource()
                            .addValue(OrderColumn.id, order.getId())
                            .addValue(OrderColumn.mobile, order.getUserModel().getMobile())
                            .addValue(transactionId, transaction.getTransactionId())
                            .addValue(shopId, order.getShopModel().getId())
                            .addValue(status, order.getOrderStatus().name())
                            .addValue(price, order.getPrice())
                            .addValue(deliveryPrice, order.getDeliveryPrice())
                            .addValue(deliveryLocation, order.getDeliveryLocation())
                            .addValue(cookingInfo, order.getCookingInfo());

                    int orderResult = namedParameterJdbcTemplate.update(OrderQuery.insertOrder, parameter);
                    if (orderResult <= 0) {
                        response.setCode(ErrorLog.ODNU1263);
                        response.setData(ErrorLog.OrderDetailNotUpdated);
                    } else {
                        int i;
                        for (i = 0; i < orderItemListModel.getOrderItemsList().size(); i++) {
                            OrderItemModel orderItem = orderItemListModel.getOrderItemsList().get(i);
                            Response<String> orderItemResult = insertOrderItem(orderItem, orderItemListModel.getOrderModel().getId());
                            if (!orderItemResult.getCode().equals(ErrorLog.CodeSuccess)) {
                                response.setCode(ErrorLog.OIDNU1262);
                                response.setData(ErrorLog.OrderItemDetailNotUpdated + " : " + orderItem);
                                break;
                            }
                        }
                        if (i == orderItemListModel.getOrderItemsList().size()) {
                            priority = Priority.LOW;
                            response.setCode(ErrorLog.CodeSuccess);
                            response.setMessage(ErrorLog.Success);
                            response.setData(ErrorLog.Success);
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1261);
            e.printStackTrace();
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), orderItemListModel.getOrderModel().getId(), orderItemListModel.toString(), priority));
        return response;
    }

    public Response<String> insertOrderItem(OrderItemModel orderItemModel, String orderId) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderItemColumn.orderId, orderId)
                    .addValue(OrderItemColumn.itemId, orderItemModel.getItemModel().getId())
                    .addValue(OrderItemColumn.quantity, orderItemModel.getQuantity())
                    .addValue(OrderItemColumn.price, orderItemModel.getPrice());

            int result = namedParameterJdbcTemplate.update(OrderItemQuery.insertOrderItem, parameter);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> verifyOrder(OrderItemListModel orderItemListModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1053);
                response.setMessage(ErrorLog.InvalidHeader);
            } else {
                OrderModel order = orderItemListModel.getOrderModel();
                ShopModel shopModel = order.getShopModel();

                Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(shopModel);
                if (!configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.SDNA1265);
                    response.setMessage(ErrorLog.ShopDetailNotAvailable);
                } else {
                    ConfigurationModel configurationModel = configurationModelResponse.getData();
                    if (configurationModel.getIsOrderTaken() != 1) {
                        response.setCode(ErrorLog.ONT1266);
                        response.setMessage(ErrorLog.OrderNotTaken);
                    } else {
                        String deliveryResponse = verifyPricing(orderItemListModel, configurationModel);
                        if (!deliveryResponse.equals(ErrorLog.Success)) {
                            response.setCode(ErrorLog.OPM1267);
                            response.setMessage(ErrorLog.OrderPriceMismatch);
                            response.setData(deliveryResponse);
                        } else {
                            priority = Priority.LOW;
                            response.setCode(ErrorLog.CodeSuccess);
                            response.setMessage(ErrorLog.Success);
                            response.setData(ErrorLog.Success);
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1268);
            e.printStackTrace();
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), orderItemListModel.getOrderModel().getId(), orderItemListModel.toString(), priority));
        return response;
    }

    /**************************************************/

    public Response<List<OrderItemListModel>> getOrderByMobile(String mobile, Integer pageNum, Integer pageCount, RequestHeaderModel requestHeaderModel) {
        Response<List<OrderItemListModel>> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        List<OrderModel> orderModelList = null;
        List<OrderItemListModel> orderItemListByMobile = null;

        try {
            if (!requestHeaderModel.getRole().equals((UserRole.CUSTOMER).name())) {
                response.setCode(ErrorLog.IH1054);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1055);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.mobile, mobile)
                        .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                        .addValue(OrderQuery.pageCount, pageCount);

                try {
                    orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByMobile, parameter, OrderRowMapperLambda.orderRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(ErrorLog.CE1269);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1270);
            e.printStackTrace();
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                orderItemListByMobile = new ArrayList<>();

                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                    Response<ShopModel> shopModelResponse = shopDao.getShopById(orderModel.getShopModel().getId());
                    Response<List<OrderItemModel>> orderItemsListResponse = itemDao.getItemsByOrderId(orderModel);

                    if (!transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.TDNA1271);
                        response.setMessage(ErrorLog.TransactionDetailNotAvailable);
                        break;
                    } else
                        orderModel.setTransactionModel(transactionModelResponse.getData());

                    if (!shopModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.SDNA1272);
                        response.setMessage(ErrorLog.ShopDetailNotAvailable);
                        break;
                    } else {
                        shopModelResponse.getData().setCollegeModel(null);
                        orderModel.setShopModel(shopModelResponse.getData());
                    }

                    if (!orderItemsListResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.OIDNA1273);
                        response.setMessage(ErrorLog.OrderItemDetailNotAvailable);
                        break;
                    } else {
                        OrderItemListModel orderItemListModel = new OrderItemListModel();
                        orderModel.setUserModel(null);
                        orderItemListModel.setOrderModel(orderModel);
                        orderItemListModel.setOrderItemsList(orderItemsListResponse.getData());
                        orderItemListByMobile.add(orderItemListModel);
                    }
                }
                response.setData(orderItemListByMobile);
            }
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), null, mobile + "-" + pageNum, priority));
        return response;
    }

    public Response<List<OrderModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, RequestHeaderModel requestHeaderModel) {
        Response<List<OrderModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals((UserRole.CUSTOMER).name())) {
                response.setCode(ErrorLog.IH1056);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1057);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.shopId, shopId)
                        .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                        .addValue(OrderQuery.pageCount, pageCount);

                try {
                    orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByShopIdPagination, parameter, OrderRowMapperLambda.orderRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(ErrorLog.CE1274);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1275);
            e.printStackTrace();
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                for (OrderModel orderModel : orderModelList) {
                    orderModel.setShopModel(null);
                    Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                    Response<UserModel> userModelResponse = userDao.getUserByMobile(orderModel.getUserModel().getMobile());

                    if (!transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.TDNA1276);
                        response.setMessage(ErrorLog.TransactionDetailNotAvailable);
                        break;
                    } else
                        orderModel.setTransactionModel(transactionModelResponse.getData());

                    if (!userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.UDNA1277);
                        response.setMessage(ErrorLog.UserDetailNotAvailable);
                        break;
                    } else
                        orderModel.setUserModel(userModelResponse.getData());
                }
                response.setData(orderModelList);
            }
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), null, mobile + "-" + pageNum, priority));
        return response;
    }

    public Response<List<OrderModel>> getOrderByShopId(Integer shopId, RequestHeaderModel requestHeaderModel) {
        Response<List<OrderModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals((UserRole.CUSTOMER).name())) {
                response.setCode(ErrorLog.IH1020);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1021);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.shopId, shopId);
                try {
                    orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByShopId, parameter, OrderRowMapperLambda.orderRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(ErrorLog.ODNA1287);
                    response.setMessage(ErrorLog.OrderDetailNotAvailable);
                    e.printStackTrace();
                }
            }

        } catch (Exception e1) {
            response.setCode(ErrorLog.CE1289);
            e1.printStackTrace();
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                for (OrderModel orderModel : orderModelList) {
                    orderModel.setShopModel(null);
                    Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                    Response<UserModel> userModelResponse = userDao.getUserByMobile(orderModel.getUserModel().getMobile());
                    if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        orderModel.setTransactionModel(transactionModelResponse.getData());
                        if (userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            orderModel.setUserModel(userModelResponse.getData());
                        } else {
                            priority = Priority.MEDIUM;
                            response.setCode(ErrorLog.UDNA1290);
                            response.setMessage(ErrorLog.UserDetailNotAvailable);
                        }
                    } else {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.TDNA1291);
                        response.setMessage(ErrorLog.TransactionDetailNotAvailable);
                    }
                }
                response.setData(orderModelList);
            }
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), shopId.toString(), shopId.toString(), priority));
        return response;
    }

    public Response<OrderModel> getOrderById(String id, RequestHeaderModel requestHeaderModel) {
        Response<OrderModel> response = new Response<>();
        OrderModel orderModel = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1058);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.id, id);

                try {
                    orderModel = namedParameterJdbcTemplate.queryForObject(OrderQuery.getOrderByOrderId, parameter, OrderRowMapperLambda.orderRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(ErrorLog.CE1278);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1279);
            e.printStackTrace();
        } finally {
            if (orderModel != null) {
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                Response<UserModel> userModelResponse = userDao.getUserByMobile(orderModel.getUserModel().getMobile());
                Response<ShopModel> shopModelResponse = shopDao.getShopById(orderModel.getShopModel().getId());

                if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    orderModel.setTransactionModel(transactionModelResponse.getData());
                    if (userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        orderModel.setUserModel(userModelResponse.getData());
                        if (shopModelResponse.getCode().equals(ErrorLog.CodeSuccess))
                            orderModel.setShopModel(shopModelResponse.getData());
                        else {
                            priority = Priority.MEDIUM;
                            response.setCode(ErrorLog.SDNA1294);
                            response.setMessage(ErrorLog.ShopDetailNotAvailable);
                        }
                    } else {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.UDNA1293);
                        response.setMessage(ErrorLog.UserDetailNotAvailable);
                    }
                } else {
                    priority = Priority.MEDIUM;
                    response.setCode(ErrorLog.TDNA1292);
                    response.setMessage(ErrorLog.TransactionDetailNotAvailable);
                }
                response.setData(orderModel);
            }
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), id, null, priority));
        return response;
    }

    /**************************************************/

    public Response<String> updateOrderRating(OrderModel orderModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1019);
                response.setData(ErrorLog.InvalidHeader);
            } else {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.rating, orderModel.getRating())
                        .addValue(id, orderModel.getId());

                int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrderRating, parameter);
                if (updateStatus > 0) {
                    ratingDao.updateShopRating(orderModel.getShopModel().getId(), orderModel.getRating());

                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(ErrorLog.ODNU1285);
                    response.setData(ErrorLog.OrderDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1286);
            e.printStackTrace();
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), orderModel.getId(), orderModel.toString(), priority));
        return response;
    }

    public Response<String> updateOrderKey(OrderModel orderModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1022);
                response.setMessage(ErrorLog.InvalidHeader);
            } else {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.secretKey, orderModel.getSecretKey())
                        .addValue(id, orderModel.getId());

                int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrderKey, parameter);
                if (updateStatus > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(ErrorLog.ODNU1295);
                    response.setMessage(ErrorLog.OrderDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1296);
            e.printStackTrace();
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), orderModel.getId(), orderModel.toString(), priority));
        return response;
    }

    public Response<String> updateOrderStatus(OrderModel orderModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
            } else {
                Response<OrderModel> orderModelResponse = getOrderById(orderModel.getId(), requestHeaderModel);
                if (orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    if (checkOrderStatusValidity(orderModelResponse.getData().getOrderStatus(), orderModel.getOrderStatus())) {
                        if (orderModel.getOrderStatus().equals(OrderStatus.READY) || orderModel.getOrderStatus().equals(OrderStatus.OUT_FOR_DELIVERY)) {
                            String secretKey = Integer.toString(100000 + new Random().nextInt(900000));
                            orderModelResponse.getData().setSecretKey(secretKey);
                            Response<String> updateResponse = updateOrderKey(orderModelResponse.getData(), requestHeaderModel);
                            if (!updateResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                                response.setCode(ErrorLog.ODNU1280);
                                response.setData(ErrorLog.OrderDetailNotUpdated);
                            }
                        }

                        if (orderModel.getOrderStatus().equals(OrderStatus.COMPLETED) || orderModel.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                            if (!orderModel.getSecretKey().equals(orderModelResponse.getData().getSecretKey())) {
                                response.setCode(ErrorLog.SKM1281);
                                response.setData(ErrorLog.SecretKeyMismatch);
                            }
                        }

                        MapSqlParameterSource parameter = new MapSqlParameterSource()
                                .addValue(status, orderModel.getOrderStatus().name())
                                .addValue(id, orderModel.getId());

                        namedParameterJdbcTemplate.update(OrderQuery.updateOrderStatus, parameter);

                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(ErrorLog.Success);
                        priority = Priority.LOW;

                    } else {
                        response.setCode(ErrorLog.IOS1282);
                        response.setData(ErrorLog.InvalidOrderStatus);
                    }
                } else {
                    response.setCode(ErrorLog.ODNA1283);
                    response.setData(ErrorLog.OrderDetailNotAvailable);
                }
            }

        } catch (Exception e) {
            response.setCode(ErrorLog.CE1284);
            e.printStackTrace();
        }

        auditLogDao.insertOrderLog(new OrderLogModel(response, requestHeaderModel.getMobile(), orderModel.getId(), orderModel.toString(), priority));
        return response;
    }

    /**************************************************/

    boolean checkOrderStatusValidity(OrderStatus currentStatus, OrderStatus newStatus) {
        // starting states -> failure,pending,placed
        // terminal states -> cancelled by seller or user, delivered, completed

        // pending -> failure ,placed
        // placed  -> cancelled by user or seller , accepted
        // cancelled by user or seller -> refund table entry must be added
        // accepted -> ready, out_for_delivery , cancelled by seller -> refund table entry must be added
        // ready -> secret key must be updated in table, completed
        // out_for_delivery -> secret key must be updated in table, delivered

        if (currentStatus == null)
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PENDING) || newStatus.equals(OrderStatus.PLACED);

        else if (currentStatus.equals(OrderStatus.PENDING))
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PLACED);
        else if (currentStatus.equals(OrderStatus.PLACED)) {
            return newStatus.equals(OrderStatus.CANCELLED_BY_SELLER) || newStatus.equals(OrderStatus.CANCELLED_BY_USER) || newStatus.equals(OrderStatus.ACCEPTED);
        } else if (currentStatus.equals(OrderStatus.ACCEPTED)) {
            return newStatus.equals(OrderStatus.READY) || newStatus.equals(OrderStatus.OUT_FOR_DELIVERY) || newStatus.equals(OrderStatus.CANCELLED_BY_SELLER);
        } else if (currentStatus.equals(OrderStatus.READY)) {
            return newStatus.equals(OrderStatus.COMPLETED);
        } else if (currentStatus.equals(OrderStatus.OUT_FOR_DELIVERY)) {
            return newStatus.equals(OrderStatus.DELIVERED);
        }
        return false;
    }

    public String verifyPricing(OrderItemListModel orderItemListModel, ConfigurationModel configurationModel) {
        Double deliveryPrice = 0.0;
        OrderModel order = orderItemListModel.getOrderModel();

        if (order.getDeliveryPrice() != null) {
            if (configurationModel.getIsDeliveryAvailable() != 1)
                return ErrorLog.DeliveryNotAvailable;

            if (!configurationModel.getDeliveryPrice().equals(order.getDeliveryPrice()))
                return ErrorLog.OrderDeliveryPriceMismatch;

            deliveryPrice = order.getDeliveryPrice();
        }

        Double totalPrice = calculatePricing(orderItemListModel.getOrderItemsList());
        if (totalPrice == null)
            return ErrorLog.ItemPriceMismatch;

        if (totalPrice + deliveryPrice != order.getPrice())
            return ErrorLog.OrderPriceMismatch;

        return ErrorLog.Success;
    }

    public Double calculatePricing(List<OrderItemModel> orderItemModelList) {
        Double totalPrice = 0.0;
        for (OrderItemModel orderItemModel : orderItemModelList) {
            Response<ItemModel> itemModelResponse = itemDao.getItemById(orderItemModel.getItemModel().getId());
            if (!itemModelResponse.getCode().equals(ErrorLog.CodeSuccess))
                return null;
            totalPrice += orderItemModel.getQuantity() * itemModelResponse.getData().getPrice();
        }
        return totalPrice;
    }
}
