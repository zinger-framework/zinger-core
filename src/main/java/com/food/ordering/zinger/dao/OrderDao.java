package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.OrderColumn;
import com.food.ordering.zinger.column.OrderItemColumn;
import com.food.ordering.zinger.enums.OrderStatus;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.*;
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
    UserDao userDao;

    @Autowired
    ConfigurationDao configurationDao;

    public Response<String> insertOrder(OrderItemListModel orderItemListModel, RequestHeaderModel responseHeader) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            OrderModel order = orderItemListModel.getOrderModel();
            TransactionModel transaction = order.getTransactionModel();

            Response<String> transactionResult = transactionDao.insertTransactionDetails(transaction);
            if (!transactionResult.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setData(ErrorLog.TransactionDetailNotUpdated);
                return response;
            }

            if (transaction.getResponseCode().equals(PaytmResponseLog.TxnSuccessfulCode) && transaction.getResponseMessage().equals(PaytmResponseLog.TxnSuccessful) && checkOrderStatusValidity(null, order.getOrderStatus())) {
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
                    response.setData(ErrorLog.OrderDetailNotUpdated);
                    return response;
                }

                for (OrderItemModel orderItem : orderItemListModel.getOrderItemsList()) {
                    Response<String> orderItemResult = insertOrderItem(orderItem, orderItemListModel.getOrderModel().getId());
                    if (!orderItemResult.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setData(ErrorLog.OrderItemDetailNotUpdated + " : " + orderItem);
                        return response;
                    }
                }
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public Response<String> verifyOrder(OrderItemListModel orderItemListModel, RequestHeaderModel responseHeader) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            OrderModel order = orderItemListModel.getOrderModel();
            ShopModel shopModel = order.getShopModel();

            Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(shopModel);
            if (!configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.ShopDetailNotAvailable);
                return response;
            }

            ConfigurationModel configurationModel = configurationModelResponse.getData();
            if (configurationModel.getIsOrderTaken() != 1) {
                response.setMessage(ErrorLog.OrderNotTaken);
                return response;
            }

            String deliveryResponse = verifyPricing(orderItemListModel, configurationModel);
            if (!deliveryResponse.equals(ErrorLog.Success)) {
                response.setData(deliveryResponse);
                return response;
            }

            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.Success);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**************************************************/

    public Response<List<OrderItemListModel>> getOrderByMobile(String mobile, Integer pageNum, Integer pageCount, RequestHeaderModel responseHeader) {
        Response<List<OrderItemListModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;
        List<OrderItemListModel> orderItemListByMobile = null;

        try {
            if (!responseHeader.getRole().equals((UserRole.CUSTOMER).name())) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.mobile, mobile)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            try {
                orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByMobile, parameter, OrderRowMapperLambda.orderRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                orderItemListByMobile = new ArrayList<>();

                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                    Response<ShopModel> shopModelResponse = shopDao.getShopById(orderModel.getShopModel().getId());
                    Response<List<OrderItemModel>> orderItemsListResponse = itemDao.getItemsByOrderId(orderModel);

                    if (!transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setMessage(ErrorLog.TransactionDetailNotAvailable);
                        return response;
                    }

                    if (!shopModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setMessage(ErrorLog.ShopDetailNotAvailable);
                        return response;
                    }

                    if (!orderItemsListResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setMessage(ErrorLog.OrderItemDetailNotAvailable);
                        return response;
                    }


                    orderModel.setTransactionModel(transactionModelResponse.getData());
                    orderModel.setShopModel(shopModelResponse.getData());


                    OrderItemListModel orderItemListModel = new OrderItemListModel();
                    orderItemListModel.setOrderModel(orderModel);
                    orderItemListModel.setOrderItemsList(orderItemsListResponse.getData());

                    orderItemListByMobile.add(orderItemListModel);
                }
                response.setData(orderItemListByMobile);
            }
        }

        return response;
    }

    public Response<List<OrderModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, RequestHeaderModel responseHeader) {
        Response<List<OrderModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;

        try {
            if (responseHeader.getRole().equals((UserRole.CUSTOMER).name())) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.shopId, shopId)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            try {
                orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByShopIdPagination, parameter, OrderRowMapperLambda.orderRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                    Response<UserModel> userModelResponse = userDao.getUserByMobile(orderModel.getUserModel().getMobile());
                    if (!transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess) || !userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setMessage(ErrorLog.ShopDetailNotAvailable);
                        return response;
                    }

                    orderModel.setTransactionModel(transactionModelResponse.getData());
                    orderModel.setUserModel(userModelResponse.getData());
                }
                response.setData(orderModelList);
            }
        }

        return response;
    }

    public Response<List<OrderModel>> getOrderByShopId(Integer shopId, RequestHeaderModel responseHeader) {
        Response<List<OrderModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;

        try {
            if (responseHeader.getRole().equals((UserRole.CUSTOMER).name())) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.shopId, shopId);

            try {
                orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByShopId, parameter, OrderRowMapperLambda.orderRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                    Response<UserModel> userModelResponse = userDao.getUserByMobile(orderModel.getUserModel().getMobile());
                    if (!transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess) || !userModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setMessage(ErrorLog.ShopDetailNotAvailable);
                        return response;
                    }

                    orderModel.setTransactionModel(transactionModelResponse.getData());
                    orderModel.setUserModel(userModelResponse.getData());
                }
                response.setData(orderModelList);
            }
        }

        return response;
    }

    public Response<OrderModel> getOrderById(String id, RequestHeaderModel responseHeader) {
        Response<OrderModel> response = new Response<>();
        OrderModel orderModel = null;

        try {
            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.id, id);

            try {
                orderModel = namedParameterJdbcTemplate.queryForObject(OrderQuery.getOrderByOrderId, parameter, OrderRowMapperLambda.orderRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (orderModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                Response<TransactionModel> transactionModelResponse = transactionDao.getTransactionDetails(orderModel.getTransactionModel().getTransactionId());
                Response<UserModel> userModelResponse = userDao.getUserByMobile(orderModel.getUserModel().getMobile());
                Response<ShopModel> shopModelResponse = shopDao.getShopById(orderModel.getShopModel().getId());
                if (!transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess) || !userModelResponse.getCode().equals(ErrorLog.CodeSuccess) || !shopModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setMessage(ErrorLog.ShopDetailNotAvailable);
                    return response;
                }

                orderModel.setTransactionModel(transactionModelResponse.getData());
                orderModel.setUserModel(userModelResponse.getData());
                orderModel.setShopModel(shopModelResponse.getData());
                response.setData(orderModel);
            }
        }

        return response;
    }

    /**************************************************/

    public Response<String> updateOrder(OrderModel orderModel, RequestHeaderModel responseHeader) {
        Response<String> response = new Response<>();
        try {
            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(cookingInfo, orderModel.getCookingInfo())
                    .addValue(rating, orderModel.getRating())
                    .addValue(secretKey, orderModel.getSecretKey())
                    .addValue(id, orderModel.getId());

            int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrder, parameter);
            if (updateStatus > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<String> updateOrderStatus(OrderModel orderModel, RequestHeaderModel responseHeader) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(responseHeader).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            Response<OrderModel> orderModelResponse = getOrderById(orderModel.getId(), responseHeader);

            if (orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                if (checkOrderStatusValidity(orderModelResponse.getData().getOrderStatus(), orderModel.getOrderStatus())) {

                    if (orderModel.getOrderStatus().equals(OrderStatus.READY) || orderModel.getOrderStatus().equals(OrderStatus.OUT_FOR_DELIVERY)) {
                        String secretKey = Integer.toString(100000 + new Random().nextInt(900000));
                        orderModelResponse.getData().setSecretKey(secretKey);

                        Response<String> updateResponse = updateOrder(orderModelResponse.getData(), responseHeader);
                        if (!updateResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                            response.setData(ErrorLog.OrderDetailNotUpdated);
                            return response;
                        }
                    }

                    if (orderModel.getOrderStatus().equals(OrderStatus.COMPLETED) || orderModel.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                        if (!orderModel.getSecretKey().equals(orderModelResponse.getData().getSecretKey())) {
                            response.setData(ErrorLog.SecretKeyMismatch);
                            return response;
                        }
                    }

                    MapSqlParameterSource parameter = new MapSqlParameterSource()
                            .addValue(status, orderModel.getOrderStatus().name())
                            .addValue(id, orderModel.getId());

                    namedParameterJdbcTemplate.update(OrderQuery.updateOrderStatus, parameter);

                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                } else
                    response.setData(ErrorLog.InvalidOrderStatus);
            } else
                response.setData(ErrorLog.OrderDetailNotAvailable);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
