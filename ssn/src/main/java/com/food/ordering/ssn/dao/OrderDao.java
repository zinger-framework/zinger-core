package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.OrderColumn;
import com.food.ordering.ssn.enums.OrderStatus;
import com.food.ordering.ssn.model.OrderItemListModel;
import com.food.ordering.ssn.model.OrderItemModel;
import com.food.ordering.ssn.model.OrderModel;
import com.food.ordering.ssn.model.TransactionModel;
import com.food.ordering.ssn.query.OrderQuery;
import com.food.ordering.ssn.rowMapperLambda.OrderRowMapperLambda;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.food.ordering.ssn.column.OrderColumn.*;

@Repository
public class OrderDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    TransactionDao transactionDao;

    @Autowired
    OrderItemDao orderItemDao;

    public Response<String> insertOrderDetails(OrderItemListModel orderItemListModel, String oauthIdRh, String mobile) {

        Response<String> response = new Response<>();
        MapSqlParameterSource parameter;

        Response<String> transactionResult, orderItemResult = new Response<>();

        try {

            if (!utilsDao.validateUser(oauthIdRh, mobile).getCode().equals(ErrorLog.CodeSuccess)) {
                return response;
            }

            List<OrderItemModel> orderList = orderItemListModel.getOrderItemsList();
            OrderModel order = orderItemListModel.getOrderModel();
            TransactionModel transaction = order.getTransactionModel();

            // inserting into the transaction table
            transactionResult = transactionDao.insertTransactionDetails(transaction);
            if (!transactionResult.getCode().equals(ErrorLog.CodeSuccess)) {
                // handle in log class
            }


            // TODO replace with correct paytm response code and response message
            if (transaction.getResponseCode().equals("1") && transaction.getResponseMessage().equals("success") && checkOrderStatusValidity(null, order.getOrderStatus())) {
                // insert into orders table
                parameter = new MapSqlParameterSource().addValue(OrderColumn.mobile, mobile)
                        .addValue(transactionId, transaction.getTransactionId())
                        .addValue(shopId, order.getShopModel().getId())
                        .addValue(status, order.getOrderStatus())
                        .addValue(price, order.getPrice())
                        .addValue(deliveryPrice, order.getPrice())
                        .addValue(deliveryLocation, order.getDeliveryLocation())
                        .addValue(cookingInfo, order.getCookingInfo());

                int orderResult = jdbcTemplate.update(OrderQuery.insertOrder, parameter);

                if (orderResult <= 0) {
                    // handle in log
                }


                // insert into order items table
                for (OrderItemModel orderItem : orderItemListModel.getOrderItemsList()) {

                    orderItemResult = orderItemDao.insertOrderItem(orderItem, orderItemListModel.getOrderModel().getId());


                    if (!orderItemResult.getCode().equals(ErrorLog.CodeSuccess)) {
                        // TODO handle in audit class
                    }

                }


                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<String> updateOrderDetails(OrderModel orderModel, String oauthIdRh, String mobile) {

        Response<String> response = new Response<>();
        MapSqlParameterSource parameter;

        try {

            if (!utilsDao.validateUser(oauthIdRh, mobile).getCode().equals(ErrorLog.CodeSuccess)) {
                return response;
            }

            parameter = new MapSqlParameterSource().addValue(cookingInfo, orderModel.getCookingInfo())
                    .addValue(rating, orderModel.getRating())
                    .addValue(secretKey, orderModel.getSecretKey())
                    .addValue(id, orderModel.getId());

            int updateStatus = jdbcTemplate.update(OrderQuery.updateOrder, parameter);

            if (updateStatus > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthIdRH, String mobile) {

        Response<String> response = new Response<>();
        MapSqlParameterSource parameter;
        OrderModel currentOrderDetails;

        try {
            if (!utilsDao.validateUser(oauthIdRH, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;


            parameter = new MapSqlParameterSource().addValue(id, orderModel.getId());
            currentOrderDetails = jdbcTemplate.queryForObject(OrderQuery.getOrderByOrderId, parameter, OrderRowMapperLambda.orderRowMapperLambda);

            if (currentOrderDetails != null) {

                if (checkOrderStatusValidity(currentOrderDetails.getOrderStatus(), orderModel.getOrderStatus())) {
                    if (orderModel.getSecretKey() != null) {
                        if (orderModel.getSecretKey() != currentOrderDetails.getSecretKey()) {
                            response.setData(ErrorLog.SecretKeyMismatch);
                            return response;
                        }
                    }


                    parameter = new MapSqlParameterSource().addValue(status, orderModel.getOrderStatus())
                            .addValue(id, orderModel.getId());

                    jdbcTemplate.update(OrderQuery.updateOrderStatus, parameter);

                    response.setMessage(ErrorLog.Success);
                    response.setCode(ErrorLog.CodeSuccess);

                } else {
                    response.setMessage(ErrorLog.OrderStateChangeNotValid);
                }

            } else {
                response.setMessage(ErrorLog.OrderDetailsNotAvailable);
            }


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

        if (currentStatus == null) {
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PENDING) || newStatus.equals(OrderStatus.PLACED);
        }

        if (currentStatus.equals(newStatus))
            return true;

        else if (currentStatus.equals(OrderStatus.PENDING)) {
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PLACED);
        } else if (currentStatus.equals(OrderStatus.PLACED)) {
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


}
