package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.OrderColumn;
import com.food.ordering.ssn.column.OrderItemColumn;
import com.food.ordering.ssn.model.OrderItemListModel;
import com.food.ordering.ssn.model.OrderItemModel;
import com.food.ordering.ssn.model.OrderModel;
import com.food.ordering.ssn.model.TransactionModel;
import com.food.ordering.ssn.query.OrderQuery;
import com.food.ordering.ssn.query.TransactionQuery;
import com.food.ordering.ssn.rowMapperLambda.OrderRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.TransactionRowMapperLambda;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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




}
