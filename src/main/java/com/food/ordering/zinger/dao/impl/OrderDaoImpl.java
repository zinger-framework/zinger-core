package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column;
import com.food.ordering.zinger.constant.Column.OrderColumn;
import com.food.ordering.zinger.constant.Column.OrderItemColumn;
import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.Enums.OrderStatus;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.OrderItemQuery;
import com.food.ordering.zinger.constant.Query.OrderQuery;
import com.food.ordering.zinger.constant.Query.TransactionQuery;
import com.food.ordering.zinger.dao.interfaces.*;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.OrderLogModel;
import com.food.ordering.zinger.rowMapperLambda.OrderRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.TransactionRowMapperLambda;
import com.food.ordering.zinger.utils.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.food.ordering.zinger.constant.Column.OrderColumn.*;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    InterceptorDaoImpl interceptorDaoImpl;

    @Autowired
    TransactionDaoImpl transactionDaoImpl;

    @Autowired
    ItemDaoImpl itemDaoImpl;

    @Autowired
    ShopDaoImpl shopDaoImpl;

    @Autowired
    RatingDaoImpl ratingDaoImpl;

    @Autowired
    UserDaoImpl userDaoImpl;

    @Autowired
    AuditLogDaoImpl auditLogDaoImpl;

    @Autowired
    ConfigurationDaoImpl configurationDaoImpl;

    @Autowired
    Environment env;

    @Autowired
    PaymentResponse paymentResponse;

    @Override
    public Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel) {
        /*
         *   1. Verify the amount and availability of the items using verify order
         *   2. Generate the transaction token from payment gateway
         *   3. Insert the order
         * */

        Response<TransactionTokenModel> response = new Response<>();
        TransactionTokenModel transactionTokenModel = new TransactionTokenModel();
        Priority priority = Priority.HIGH;

        try {
            Response<String> verifyOrderResponse = verifyOrderDetails(orderItemListModel);

            if (verifyOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {

                Response<String> initiateTransactionResponse = initiateTransaction(orderItemListModel.getTransactionModel().getOrderModel(), verifyOrderResponse.getData());

                if (initiateTransactionResponse.getCode().equals(ErrorLog.CodeSuccess)) {

                    OrderModel order = orderItemListModel.getTransactionModel().getOrderModel();
                    MapSqlParameterSource parameter = new MapSqlParameterSource()
                            .addValue(userId, order.getUserModel().getId())
                            .addValue(shopId, order.getShopModel().getId())
                            .addValue(price, order.getPrice())
                            .addValue(deliveryPrice, order.getDeliveryPrice())
                            .addValue(deliveryLocation, order.getDeliveryLocation())
                            .addValue(cookingInfo, order.getCookingInfo());

                    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate());
                    simpleJdbcInsert.withTableName(tableName).usingGeneratedKeyColumns(Column.OrderColumn.id);
                    Number responseValue = simpleJdbcInsert.executeAndReturnKey(parameter);

                    if (responseValue.intValue() <= 0) {
                        response.setCode(ErrorLog.ODNU1263);
                        response.setMessage(ErrorLog.OrderDetailNotUpdated);
                    } else {
                        orderItemListModel.getTransactionModel().getOrderModel().setId(responseValue.intValue());
                        Response<String> orderInsertResponse = insertOrderItem(orderItemListModel);
                        if (orderInsertResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            response.setCode(orderInsertResponse.getCode());
                            response.setMessage(orderInsertResponse.getMessage());

                            transactionTokenModel.setOrderId(responseValue.intValue());
                            transactionTokenModel.setTransactionToken(initiateTransactionResponse.getData());
                            response.setData(transactionTokenModel);
                        } else {
                            response.setCode(ErrorLog.OIDNU1296);
                            response.setMessage(ErrorLog.OrderItemDetailNotUpdated);
                        }
                    }
                } else {
                    response.setCode(ErrorLog.TIF1300);
                    response.setMessage(ErrorLog.TransactionInitiationFailed);
                }

            } else {
                response.setCode(verifyOrderResponse.getCode());
                response.setMessage(verifyOrderResponse.getMessage());
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1261);
            response.setMessage(ErrorLog.TransactionTokenNotAvailable);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, orderItemListModel.getTransactionModel().getOrderModel().getId(), orderItemListModel.toString(), priority));
        return response;
    }

    public Response<String> insertOrderItem(OrderItemListModel orderItemModelList) {
        Response<String> response = new Response<>();
        Integer orderId = orderItemModelList.getTransactionModel().getOrderModel().getId();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource();
            for (int i = 0; i < orderItemModelList.getOrderItemsList().size(); i++) {
                OrderItemModel orderItemModel = orderItemModelList.getOrderItemsList().get(i);
                parameter.addValue(OrderItemColumn.orderId + i, orderId)
                        .addValue(OrderItemColumn.itemId + i, orderItemModel.getItemModel().getId())
                        .addValue(OrderItemColumn.quantity + i, orderItemModel.getQuantity())
                        .addValue(OrderItemColumn.price + i, orderItemModel.getPrice());
            }

            int result = namedParameterJdbcTemplate.update(OrderItemQuery.getInsertOrder(orderItemModelList.getOrderItemsList()), parameter);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return response;
    }

    /**************************************************/

    @Override
    public Response<String> placeOrder(Integer orderId) {
        /*
         *   1. verify the transaction status api and
         *   2. Insert the transaction in the transaction table
         * */
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            Response<TransactionModel> verifyOrderResponse = verifyOrder(orderId, 2);

            if (verifyOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                Response<String> insertTransactionResponse = transactionDaoImpl.insertTransactionDetails(verifyOrderResponse.getData());

                if (insertTransactionResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    Response<String> updateOrderStatusResponse = updateOrderStatus(verifyOrderResponse.getData().getOrderModel());

                    if (updateOrderStatusResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(ErrorLog.Success);
                        priority = Priority.LOW;
                    } else {
                        response.setCode(ErrorLog.ODNU1156);
                        response.setMessage(ErrorLog.OrderDetailNotUpdated);
                    }
                } else {
                    response.setCode(ErrorLog.TDNU1264);
                    response.setMessage(ErrorLog.TransactionDetailNotUpdated);
                }
            } else {
                response.setCode(ErrorLog.ODNA1167);
                response.setMessage(ErrorLog.OrderDetailNotAvailable);
                priority = Priority.HIGH;
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1109);
            priority = Priority.HIGH;
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, null, orderId.toString(), priority));
        return response;
    }

    /**************************************************/

    @Override
    public Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        List<TransactionModel> transactionModelList = null;
        List<OrderItemListModel> orderItemListByMobile;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.userId, userId)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            try {
                transactionModelList = namedParameterJdbcTemplate.query(TransactionQuery.getTransactionByUserId, parameter, TransactionRowMapperLambda.transactionRowMapperLambda);
            } catch (Exception e) {
                response.setCode(ErrorLog.CE1269);
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1270);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (transactionModelList != null && !transactionModelList.isEmpty()) {
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                orderItemListByMobile = new ArrayList<>();

                for (TransactionModel transactionModel : transactionModelList) {
                    Response<OrderModel> orderModelResponse = getOrderDetailById(transactionModel.getOrderModel().getId());

                    if (!orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.ODNA1271);
                        response.setMessage(ErrorLog.OrderDetailNotAvailable);
                        break;
                    } else {
                        transactionModel.setOrderModel(orderModelResponse.getData());
                        Response<ShopModel> shopModelResponse = shopDaoImpl.getShopById(transactionModel.getOrderModel().getShopModel().getId());
                        if (!shopModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            priority = Priority.MEDIUM;
                            response.setCode(ErrorLog.SDNA1272);
                            response.setMessage(ErrorLog.ShopDetailNotAvailable);
                            break;
                        } else {
                            shopModelResponse.getData().setPlaceModel(null);
                            transactionModel.getOrderModel().setShopModel(shopModelResponse.getData());
                            Response<List<OrderItemModel>> orderItemsListResponse = itemDaoImpl.getItemsByOrderId(transactionModel.getOrderModel());
                            if (!orderItemsListResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                                priority = Priority.MEDIUM;
                                response.setCode(ErrorLog.OIDNA1273);
                                response.setMessage(ErrorLog.OrderItemDetailNotAvailable);
                                break;
                            } else {
                                OrderItemListModel orderItemListModel = new OrderItemListModel();
                                transactionModel.getOrderModel().setUserModel(null);
                                orderItemListModel.setTransactionModel(transactionModel);
                                orderItemListModel.setOrderItemsList(orderItemsListResponse.getData());
                                orderItemListByMobile.add(orderItemListModel);
                            }
                        }
                    }
                }
                response.setData(orderItemListByMobile);
            }
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, null, userId + "-" + pageNum, priority));
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount) {

        Response<List<OrderItemListModel>> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        List<TransactionModel> transactionModelList = null;
        List<OrderItemListModel> orderItemListByMobile;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.shopId, shopId)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            transactionModelList = namedParameterJdbcTemplate.query(TransactionQuery.getTransactionByShopIdPagination, parameter, TransactionRowMapperLambda.transactionRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1274);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (transactionModelList != null && !transactionModelList.isEmpty()) {
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                orderItemListByMobile = new ArrayList<>();

                for (TransactionModel transactionModel : transactionModelList) {
                    Response<OrderModel> orderModelResponse = getOrderDetailById(transactionModel.getOrderModel().getId());

                    if (!orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.ODNA1276);
                        response.setMessage(ErrorLog.OrderDetailNotAvailable);
                        break;
                    } else {
                        transactionModel.setOrderModel(orderModelResponse.getData());
                        Response<UserModel> userModelResponse = userDaoImpl.getUserByMobile(transactionModel.getOrderModel().getUserModel().getMobile());
                        if (!userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            priority = Priority.MEDIUM;
                            response.setCode(ErrorLog.UDNA1277);
                            response.setMessage(ErrorLog.UserDetailNotAvailable);
                            break;
                        } else {
                            transactionModel.getOrderModel().setUserModel(userModelResponse.getData());
                            Response<List<OrderItemModel>> orderItemsListResponse = itemDaoImpl.getItemsByOrderId(transactionModel.getOrderModel());
                            if (!orderItemsListResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                                priority = Priority.MEDIUM;
                                response.setCode(ErrorLog.OIDNA1297);
                                response.setMessage(ErrorLog.OrderItemDetailNotAvailable);
                                break;
                            } else {
                                OrderItemListModel orderItemListModel = new OrderItemListModel();
                                transactionModel.getOrderModel().setShopModel(null);
                                orderItemListModel.setTransactionModel(transactionModel);
                                orderItemListModel.setOrderItemsList(orderItemsListResponse.getData());
                                orderItemListByMobile.add(orderItemListModel);
                            }
                        }
                    }
                }
                response.setData(orderItemListByMobile);
            }
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, null, shopId + "-" + pageNum, priority));
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId) {

        Response<List<OrderItemListModel>> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        List<TransactionModel> transactionModelList = null;
        List<OrderItemListModel> orderItemListByMobile;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.shopId, shopId);
            try {
                transactionModelList = namedParameterJdbcTemplate.query(TransactionQuery.getTransactionByShopId, parameter, TransactionRowMapperLambda.transactionRowMapperLambda);
            } catch (Exception e) {
                response.setCode(ErrorLog.ODNA1287);
                response.setMessage(ErrorLog.OrderDetailNotAvailable);
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1289);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (transactionModelList != null && !transactionModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                orderItemListByMobile = new ArrayList<>();

                for (TransactionModel transactionModel : transactionModelList) {

                    Response<OrderModel> orderModelResponse = getOrderDetailById(transactionModel.getOrderModel().getId());

                    if (!orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        priority = Priority.MEDIUM;
                        response.setCode(ErrorLog.ODNA1291);
                        response.setMessage(ErrorLog.OrderDetailNotAvailable);
                        break;
                    } else {
                        transactionModel.setOrderModel(orderModelResponse.getData());
                        Response<UserModel> userModelResponse = userDaoImpl.getUserByMobile(transactionModel.getOrderModel().getUserModel().getMobile());
                        if (!userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            priority = Priority.MEDIUM;
                            response.setCode(ErrorLog.UDNA1290);
                            response.setMessage(ErrorLog.UserDetailNotAvailable);
                            break;
                        } else {
                            transactionModel.getOrderModel().setUserModel(userModelResponse.getData());
                            Response<List<OrderItemModel>> orderItemsListResponse = itemDaoImpl.getItemsByOrderId(transactionModel.getOrderModel());

                            if (!orderItemsListResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                                priority = Priority.MEDIUM;
                                response.setCode(ErrorLog.OIDNA1298);
                                response.setMessage(ErrorLog.OrderItemDetailNotAvailable);
                                break;
                            } else {
                                OrderItemListModel orderItemListModel = new OrderItemListModel();
                                transactionModel.getOrderModel().setShopModel(null);
                                orderItemListModel.setTransactionModel(transactionModel);
                                orderItemListModel.setOrderItemsList(orderItemsListResponse.getData());
                                orderItemListByMobile.add(orderItemListModel);
                            }
                        }
                    }
                }
                response.setData(orderItemListByMobile);
            }
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, shopId, shopId.toString(), priority));
        return response;
    }

    private Response<List<OrderModel>> getOrdersByStatus(List<OrderStatus> orderStatusList) {

        Response<List<OrderModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;

        try {
            if (orderStatusList != null && orderStatusList.size() > 0)
                orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByStatus(orderStatusList), OrderRowMapperLambda.orderRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.ODNA1299);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderModelList);
            }
        }

        return response;
    }

    @Override
    public Response<TransactionModel> getOrderById(Integer orderId) {
        Response<TransactionModel> response = new Response<>();
        TransactionModel transactionModel;
        Priority priority = Priority.MEDIUM;

        try {
            Response<TransactionModel> transactionModelResponse = transactionDaoImpl.getTransactionByOrderId(orderId);

            if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                transactionModel = transactionModelResponse.getData();
                Response<OrderModel> orderModelResponse = getOrderDetailById(orderId);

                if (orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    transactionModel.setOrderModel(orderModelResponse.getData());
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(transactionModel);
                } else {
                    response.setCode(orderModelResponse.getCode());
                    response.setMessage(orderModelResponse.getMessage());
                }
            } else {
                response.setCode(ErrorLog.TDNA1292);
                response.setMessage(ErrorLog.TransactionDetailNotAvailable);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1279);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, orderId, null, priority));
        return response;
    }

    public Response<OrderModel> getOrderDetailById(Integer id) {
        Response<OrderModel> response = new Response<>();
        OrderModel orderModel = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.id, id);

            try {
                orderModel = namedParameterJdbcTemplate.queryForObject(OrderQuery.getOrderByOrderId, parameter, OrderRowMapperLambda.orderRowMapperLambda);
            } catch (Exception e) {
                response.setCode(ErrorLog.CE1278);
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1279);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderModel != null) {
                Response<UserModel> userModelResponse = userDaoImpl.getUserById(orderModel.getUserModel().getId());
                Response<ShopModel> shopModelResponse = shopDaoImpl.getShopById(orderModel.getShopModel().getId());

                if (userModelResponse.getCode().equals(ErrorLog.CodeSuccess) && shopModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    orderModel.setUserModel(userModelResponse.getData());
                    orderModel.setShopModel(shopModelResponse.getData());

                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(orderModel);
                } else if (!userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.UDNA1293);
                    response.setMessage(ErrorLog.UserDetailNotAvailable);
                } else {
                    response.setCode(ErrorLog.SDNA1294);
                    response.setMessage(ErrorLog.ShopDetailNotAvailable);
                }
            }
        }
        return response;
    }

    /**************************************************/

    @Override
    public Response<String> updateOrderRating(OrderModel orderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            Response<TransactionModel> transactionModelResponse = getOrderById(orderModel.getId());

            if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(OrderColumn.rating, orderModel.getRating())
                        .addValue(id, orderModel.getId());

                int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrderRating, parameter);
                if (updateStatus > 0) {
                    ratingDaoImpl.updateShopRating(orderModel.getShopModel().getId(), orderModel.getRating());

                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(ErrorLog.ODNU1285);
                    response.setData(ErrorLog.OrderDetailNotUpdated);
                }
            } else {
                response.setCode(transactionModelResponse.getCode());
                response.setMessage(transactionModelResponse.getMessage());
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1286);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, orderModel.getId(), orderModel.toString(), priority));
        return response;
    }

    public Response<String> updateOrderKey(OrderModel orderModel) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.secretKey, orderModel.getSecretKey())
                    .addValue(id, orderModel.getId());

            int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrderKey, parameter);
            if (updateStatus > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response<String> updateOrderStatus(OrderModel orderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            Response<TransactionModel> transactionModelResponse = getOrderById(orderModel.getId());
            if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                OrderModel currentOrderModel = transactionModelResponse.getData().getOrderModel();

                if (checkOrderStatusValidity(currentOrderModel.getOrderStatus(), orderModel.getOrderStatus())) {
                    if (orderModel.getOrderStatus().equals(OrderStatus.READY) || orderModel.getOrderStatus().equals(OrderStatus.OUT_FOR_DELIVERY)) {
                        String secretKey = Integer.toString(100000 + new Random().nextInt(900000));
                        currentOrderModel.setSecretKey(secretKey);
                        Response<String> updateResponse = updateOrderKey(currentOrderModel);
                        if (!updateResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            response.setCode(ErrorLog.ODNU1280);
                            response.setMessage(ErrorLog.OrderDetailNotUpdated);
                        }
                    }

                    if (orderModel.getOrderStatus().equals(OrderStatus.COMPLETED) || orderModel.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                        if (!orderModel.getSecretKey().equals(currentOrderModel.getSecretKey())) {
                            response.setCode(ErrorLog.SKM1281);
                            response.setMessage(ErrorLog.SecretKeyMismatch);
                        }
                    }

                    if (orderModel.getOrderStatus().equals(OrderStatus.CANCELLED_BY_USER) || orderModel.getOrderStatus().equals(OrderStatus.CANCELLED_BY_SELLER) || orderModel.getOrderStatus().equals(OrderStatus.REFUND_INITIATED))
                        initiateRefund();

                    if (!response.getCode().equals(ErrorLog.SKM1281) && !response.getCode().equals(ErrorLog.ODNU1280)) {
                        try {
                            MapSqlParameterSource parameter = new MapSqlParameterSource()
                                    .addValue(status, orderModel.getOrderStatus().name())
                                    .addValue(id, orderModel.getId());

                            int result = namedParameterJdbcTemplate.update(OrderQuery.updateOrderStatus, parameter);
                            if (result > 0) {
                                response.setCode(ErrorLog.CodeSuccess);
                                response.setMessage(ErrorLog.Success);
                                response.setData(ErrorLog.Success);
                                priority = Priority.LOW;
                            } else {
                                response.setCode(ErrorLog.ODNU1295);
                                response.setMessage(ErrorLog.OrderDetailNotUpdated);
                            }
                        } catch (Exception e) {
                            response.setCode(ErrorLog.CE1283);
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        }
                    }
                } else {
                    response.setCode(ErrorLog.IOS1282);
                    response.setMessage(ErrorLog.InvalidOrderStatus);
                }

            } else {
                response.setCode(transactionModelResponse.getCode());
                response.setMessage(transactionModelResponse.getMessage());
            }

        } catch (Exception e) {
            response.setCode(ErrorLog.CE1284);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDaoImpl.insertOrderLog(new OrderLogModel(response, orderModel.getId(), orderModel.toString(), priority));
        return response;
    }

    public void updatePendingOrder() {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(env.getProperty(Constant.authIdSA), Integer.parseInt(env.getProperty(Constant.idSA)), env.getProperty(Constant.roleSA));
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderStatus.PENDING);
        Response<List<OrderModel>> pendingOrderResponse = getOrdersByStatus(orderStatuses);

        if (pendingOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {
            List<OrderModel> orderModelList = pendingOrderResponse.getData();

            if (orderModelList != null && orderModelList.size() > 0) {
                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = verifyOrder(orderModel.getId(), 2);

                    if (transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.PLACED)) {
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - orderModel.getDate().getTime();
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000);
                        int diffInDays = (int) ((currentDate.getTime() - orderModel.getDate().getTime()) / (1000 * 60 * 60 * 24));

                        if (diffMinutes > 10 || diffHours >= 1 || diffInDays >= 1)
                            transactionModelResponse.getData().getOrderModel().setOrderStatus(OrderStatus.REFUND_INITIATED);
                    }

                    if (!transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.PENDING)) {
                        updateOrderStatus(transactionModelResponse.getData().getOrderModel());
                        transactionDaoImpl.updatePendingTransaction(transactionModelResponse.getData());
                    }
                }
            }
        }
    }

    public void updatedRefundOrder() {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(env.getProperty(Constant.authIdSA), Integer.parseInt(env.getProperty(Constant.idSA)), env.getProperty(Constant.roleSA));
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderStatus.REFUND_INITIATED);
        orderStatuses.add(OrderStatus.CANCELLED_BY_USER);
        orderStatuses.add(OrderStatus.CANCELLED_BY_SELLER);
        Response<List<OrderModel>> pendingOrderResponse = getOrdersByStatus(orderStatuses);

        if (pendingOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {
            List<OrderModel> orderModelList = pendingOrderResponse.getData();

            if (orderModelList != null && orderModelList.size() > 0) {
                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = verifyOrder(orderModel.getId(), 1);

                    if (!transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.REFUND_INITIATED) && !transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.CANCELLED_BY_SELLER) && !transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.CANCELLED_BY_USER)) {
                        updateOrderStatus(transactionModelResponse.getData().getOrderModel());
                        transactionDaoImpl.updatePendingTransaction(transactionModelResponse.getData());
                    }
                }
            }
        }
    }

    /**************************************************/

    public String verifyPricing(OrderItemListModel orderItemListModel, ConfigurationModel configurationModel) {
        Double deliveryPrice = 0.0;
        OrderModel order = orderItemListModel.getTransactionModel().getOrderModel();

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
        else if (totalPrice == -1.0)
            return ErrorLog.ItemDetailNotAvailable;

        if (totalPrice + deliveryPrice != order.getPrice())
            return ErrorLog.OrderPriceMismatch;

        return ErrorLog.Success;
    }

    public Double calculatePricing(List<OrderItemModel> orderItemModelList) {
        Double totalPrice = 0.0;
        for (OrderItemModel orderItemModel : orderItemModelList) {
            Response<ItemModel> itemModelResponse = itemDaoImpl.getItemById(orderItemModel.getItemModel().getId());
            if (!itemModelResponse.getCode().equals(ErrorLog.CodeSuccess))
                return null;
            else if (itemModelResponse.getData().getIsAvailable() == 0)
                return -1.0;
            totalPrice += orderItemModel.getQuantity() * itemModelResponse.getData().getPrice();
        }
        return totalPrice;
    }

    public Response<String> verifyOrderDetails(OrderItemListModel orderItemListModel) {

        Response<String> response = new Response<>();

        try {
            OrderModel order = orderItemListModel.getTransactionModel().getOrderModel();
            ShopModel shopModel = order.getShopModel();

            Response<ConfigurationModel> configurationModelResponse = configurationDaoImpl.getConfigurationByShopId(shopModel);
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
                        response.setCode(ErrorLog.CE1267);
                        response.setMessage(deliveryResponse);
                        response.setData(deliveryResponse);
                    } else {
                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(configurationModelResponse.getData().getMerchantId());
                    }
                }
            }

        } catch (Exception e) {
            response.setCode(ErrorLog.CE1268);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    public Response<TransactionModel> verifyOrder(Integer orderId, int flag) {
        Response<TransactionModel> response = new Response<>();
        TransactionModel transactionModel = null;

        try {
            Response<TransactionModel> transactionModelResponse;

            if (flag == 1)
                transactionModelResponse = getRefundStatus(orderId);
            else
                transactionModelResponse = getTransactionStatus(orderId);

            Response<OrderModel> orderModelResponse = getOrderDetailById(orderId);

            if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess) &&
                    orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)
                //TODO: Uncomment After PAYMENT GATEWAY INTEGRATION
                //&& orderModelResponse.getData().getPrice().equals(transactionModelResponse.getData().transactionAmountGet())
            ) {
                transactionModel = transactionModelResponse.getData();
                orderModelResponse.getData().setOrderStatus(paymentResponse.getOrderStatus(transactionModel));
                transactionModel.setOrderModel(orderModelResponse.getData());

                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(transactionModel);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    public Response<String> initiateTransaction(OrderModel orderModel, String merchantId) {
        Response<String> response = new Response<>();

        Integer orderId = orderModel.getId();

        //TODO: Implement API to get Transaction Token From Payment Gateway using OrderId & merchantId
        //String transactionToken = getTransactionToken(orderId, merchantId);

        String transactionToken = "12Abdsfds";

        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);
        response.setData(transactionToken);
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
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PLACED) || newStatus.equals(OrderStatus.REFUND_INITIATED);

        else if (currentStatus.equals(OrderStatus.PLACED))
            return newStatus.equals(OrderStatus.CANCELLED_BY_SELLER) || newStatus.equals(OrderStatus.CANCELLED_BY_USER) || newStatus.equals(OrderStatus.ACCEPTED);

        else if (currentStatus.equals(OrderStatus.ACCEPTED))
            return newStatus.equals(OrderStatus.READY) || newStatus.equals(OrderStatus.OUT_FOR_DELIVERY) || newStatus.equals(OrderStatus.CANCELLED_BY_SELLER);

        else if (currentStatus.equals(OrderStatus.READY))
            return newStatus.equals(OrderStatus.COMPLETED);

        else if (currentStatus.equals(OrderStatus.OUT_FOR_DELIVERY))
            return newStatus.equals(OrderStatus.DELIVERED);

        else if (currentStatus.equals(OrderStatus.CANCELLED_BY_USER) || currentStatus.equals(OrderStatus.CANCELLED_BY_SELLER) || currentStatus.equals(OrderStatus.REFUND_INITIATED))
            return newStatus.equals(OrderStatus.REFUND_COMPLETED);

        return false;
    }

    /**************************************************/

    public Response<TransactionModel> getTransactionStatus(Integer orderId) {
        Response<TransactionModel> transactionModelResponse = new Response<>();

        //TODO: GET Transaction Status from Payment Gateway
        TransactionModel transactionModel = new TransactionModel();

        //Populating Dummy Values Here
        transactionModel.setTransactionId("T" + orderId);
        transactionModel.setBankTransactionId("BT0001");
        transactionModel.transactionAmountSet(90.0);
        transactionModel.setCurrency("INR");
        transactionModel.setResponseCode("01");
        transactionModel.setResponseMessage("Success");
        transactionModel.setGatewayName("PAYTM");
        transactionModel.setBankName("HDFC");
        transactionModel.setPaymentMode("UPI");
        transactionModel.setChecksumHash("XXXXX");
        transactionModel.getOrderModel().setId(orderId);

        transactionModelResponse.setCode(ErrorLog.CodeSuccess);
        transactionModelResponse.setMessage(ErrorLog.Success);
        transactionModelResponse.setData(transactionModel);
        return transactionModelResponse;
    }

    public Response<TransactionModel> getRefundStatus(Integer orderId) {
        Response<TransactionModel> transactionModelResponse = new Response<>();

        //TODO: GET Transaction Status from Payment Gateway
        TransactionModel transactionModel = new TransactionModel();

        //Populating Dummy Values Here
        transactionModel.setTransactionId("T" + orderId);
        transactionModel.setBankTransactionId("BT0001");
        transactionModel.transactionAmountSet(90.0);
        transactionModel.setCurrency("INR");
        transactionModel.setResponseCode("03");
        transactionModel.setResponseMessage("Refund Completed");
        transactionModel.setGatewayName("PAYTM");
        transactionModel.setBankName("HDFC");
        transactionModel.setPaymentMode("UPI");
        transactionModel.setChecksumHash("XXXXX");
        transactionModel.getOrderModel().setId(orderId);

        transactionModelResponse.setCode(ErrorLog.CodeSuccess);
        transactionModelResponse.setMessage(ErrorLog.Success);
        transactionModelResponse.setData(transactionModel);
        return transactionModelResponse;
    }

    public void initiateRefund() {
        //TODO: Initiate the refund using payment gateway
    }
}