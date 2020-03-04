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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.food.ordering.ssn.enums.OrderStatus.*;
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

    public Response<String> insertOrderDetails(OrderItemListModel orderItemListModel,String oauthIdRh,String mobile){

        Response<String> response=new Response<>();
        MapSqlParameterSource parameter;

        Response<String> transactionResult,orderItemResult=new Response<>();

        try{

            if(!utilsDao.validateUser(oauthIdRh,mobile).getCode().equals(ErrorLog.CodeSuccess)){
                return response;
            }

            List<OrderItemModel> orderList=orderItemListModel.getOrderItemsList();
            OrderModel order=orderItemListModel.getOrderModel();
            TransactionModel transaction=order.getTransactionModel();

            // inserting into the transaction table
            transactionResult=transactionDao.insertTransactionDetails(transaction);

            // TODO replace with correct paytm response code and response message
            if(transaction.getResponseCode().equals("1") && transaction.getResponseMessage().equals("success"))
            {
                // insert into orders table
                parameter =  new MapSqlParameterSource().addValue(OrderColumn.mobile,mobile)
                                                        .addValue(transactionId,transaction.getTransactionId())
                                                        .addValue(shopId,order.getShopModel().getId())
                                                        .addValue(status,order.getOrderStatus())
                                                        .addValue(price,order.getPrice())
                                                        .addValue(deliveryPrice,order.getPrice())
                                                        .addValue(deliveryLocation,order.getDeliveryLocation())
                                                        .addValue(cookingInfo,order.getCookingInfo());

                int orderResult=jdbcTemplate.update(OrderQuery.insertOrder,parameter);

                // insert into order items table
                for(OrderItemModel orderItem:orderItemListModel.getOrderItemsList()){

                    orderItemResult=orderItemDao.insertOrderItem(orderItem,orderItemListModel.getOrderModel().getId());

                    // TODO check if the condition is handled properly
                    if(orderItemResult.getCode().equals(ErrorLog.CodeFailure) && orderItemResult.getMessage().equals(ErrorLog.Failure))
                        break;
                }

                if(orderItemResult.getCode().equals(ErrorLog.CodeFailure) || orderResult<0 || transactionResult.getCode().equals(ErrorLog.CodeFailure))
                {

                    String data="Order Item insertion Result : "+orderItemResult.getCode() +" "+orderItemResult.getMessage()+"\n";

                    if(orderResult<0)
                        data+="Order insertion Result: "+ErrorLog.CodeFailure+" "+ErrorLog.Failure+"\n";
                    else
                        data+="Order insertion Result: "+ErrorLog.CodeSuccess+" "+ErrorLog.Success+"\n";

                    data+="Transaction insertion result "+transactionResult.getCode()+" "+transactionResult.getMessage();

                    response.setData(data);
                }
                else{
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }

    public Response<String> updateOrderDetails(OrderModel orderModel,String oauthIdRh,String mobile){

        Response<String> response=new Response<>();
        MapSqlParameterSource parameter;

        try{

            if(!utilsDao.validateUser(oauthIdRh,mobile).getCode().equals(ErrorLog.CodeSuccess)){
                return response;
            }

            parameter=new MapSqlParameterSource().addValue(cookingInfo,orderModel.getCookingInfo())
                                                 .addValue(rating,orderModel.getRating())
                                                 .addValue(secretKey,orderModel.getSecretKey())
                                                 .addValue(id,orderModel.getId());

            int updateStatus=jdbcTemplate.update(OrderQuery.updateOrder,parameter);

            if(updateStatus>0){
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }


    public Response<String> updateOrderStatus(OrderModel orderModel,String oauthIdRH,String mobile){

        Response<String> response=new Response<>();
        MapSqlParameterSource parameter;
        OrderModel currentOrderDetails;

        try{
             if(!utilsDao.validateUser(oauthIdRH,mobile).getCode().equals(ErrorLog.CodeSuccess))
                 return response;


            parameter=new MapSqlParameterSource().addValue(id,orderModel.getId());
            currentOrderDetails=jdbcTemplate.queryForObject(OrderQuery.getOrderByOrderId,parameter,OrderRowMapperLambda.orderRowMapperLambda);

            if(currentOrderDetails!=null){

                if(checkOrderStatusValidity(currentOrderDetails.getOrderStatus(),orderModel.getOrderStatus()))
                {
                    parameter=new MapSqlParameterSource().addValue(status,orderModel.getOrderStatus())
                                                         .addValue(id,orderModel.getId());

                    int result=jdbcTemplate.update(OrderQuery.updateOrderStatus,parameter);

                    if(result>0){
                        response.setMessage(ErrorLog.Success);
                        response.setCode(ErrorLog.CodeSuccess);
                    }


                }else{
                    response.setMessage(ErrorLog.OrderStateChangeNotValid);
                }

            }else{
                response.setMessage(ErrorLog.OrderDetailsNotAvailable);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }


    boolean checkOrderStatusValidity(OrderStatus currentStatus,OrderStatus newStatus){


        // starting states -> failure,pending,placed
        // terminal states -> cancelled by seller or user, delivered, completed

        // pending -> failure ,placed
        // placed  -> cancelled by user or seller , accepted
        // cancelled by user or seller -> refund table entry must be added
        // accepted -> ready, out_for_delivery , cancelled by seller -> refund table entry must be added
        // ready -> secret key must be updated in table, completed
        // out_for_delivery -> secret key must be updated in table, delivered

        if(currentStatus.equals(newStatus))
            return true;

        else if(currentStatus.equals(OrderStatus.PENDING)){
            if(newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PLACED))
                return true;
        }
        else if(currentStatus.equals(OrderStatus.PLACED)){
            if(newStatus.equals(OrderStatus.CANCELLED_BY_SELLER)||newStatus.equals(OrderStatus.CANCELLED_BY_USER)||newStatus.equals(OrderStatus.ACCEPTED))
                return true;
        }
        else if(currentStatus.equals(OrderStatus.CANCELLED_BY_USER) || currentStatus.equals(OrderStatus.CANCELLED_BY_SELLER)){
            // TODO update the refund table
        }
        else if(currentStatus.equals(OrderStatus.ACCEPTED)){
            if(newStatus.equals(OrderStatus.READY) || newStatus.equals(OrderStatus.OUT_FOR_DELIVERY) || newStatus.equals(OrderStatus.CANCELLED_BY_SELLER))
                return true;
        }
        else if(currentStatus.equals(OrderStatus.READY)){
            if(newStatus.equals(OrderStatus.COMPLETED))
                return true;
        }
        else if(currentStatus.equals(OrderStatus.OUT_FOR_DELIVERY)){
            if(newStatus.equals(OrderStatus.DELIVERED))
                return true;
        }


        return false;
    }






}
