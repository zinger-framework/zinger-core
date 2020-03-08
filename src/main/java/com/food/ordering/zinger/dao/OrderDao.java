package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.OrderColumn;
import com.food.ordering.zinger.column.OrderItemColumn;
import com.food.ordering.zinger.enums.OrderStatus;
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

import java.util.List;

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
    ConfigurationDao configurationDao;

    public Response<String> insertOrder(OrderItemListModel orderItemListModel, String oauthId, String mobile, String role) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            OrderModel order = orderItemListModel.getOrderModel();
            TransactionModel transaction = order.getTransactionModel();

            Response<String> transactionResult = transactionDao.insertTransactionDetails(transaction);
            if (!transactionResult.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setData(ErrorLog.TransactionDetailNotUpdated);
                return response;
            }

            String deliveryResponse = verifyDeliveryPrice(orderItemListModel);
            if (!deliveryResponse.equals(ErrorLog.Success)) {
                response.setData(deliveryResponse);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> updateOrder(OrderModel orderModel, String oauthId, String mobile, String role) {
        Response<String> response = new Response<>();
        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess))
                return response;

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

    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthId, String mobile, String role) {
        Response<String> response = new Response<>();
        OrderModel currentOrderDetails = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(id, orderModel.getId());

            try {
                currentOrderDetails = namedParameterJdbcTemplate.queryForObject(OrderQuery.getOrderByOrderId, parameter, OrderRowMapperLambda.orderRowMapperLambda);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if (currentOrderDetails != null) {
                if (checkOrderStatusValidity(currentOrderDetails.getOrderStatus(), orderModel.getOrderStatus())) {
                    if (orderModel.getSecretKey() != null) {
                        if (!orderModel.getSecretKey().equals(currentOrderDetails.getSecretKey())) {
                            response.setData(ErrorLog.SecretKeyMismatch);
                            return response;
                        }
                    }

                    parameter = new MapSqlParameterSource()
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

        if (currentStatus.equals(newStatus))
            return true;

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

    public String verifyDeliveryPrice(OrderItemListModel orderItemListModel) {
        Double deliveryPrice = 0.0;
        OrderModel order = orderItemListModel.getOrderModel();

        if (order.getDeliveryPrice() != null) {
            Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfiguration(order.getShopModel());
            if (configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess) && configurationModelResponse.getData().getDeliveryPrice().equals(order.getDeliveryPrice()))
                deliveryPrice = order.getDeliveryPrice();
            else
                return ErrorLog.OrderDeliveryPriceMismatch;
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
