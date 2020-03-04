package com.food.ordering.ssn.service;

import com.food.ordering.ssn.dao.OrderDao;
import com.food.ordering.ssn.model.OrderItemListModel;
import com.food.ordering.ssn.model.OrderModel;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public Response<String> insertOrderDetails(OrderItemListModel orderItemListModel,String oauthIdRh,String mobile){
        return orderDao.insertOrderDetails(orderItemListModel,oauthIdRh,mobile);
    }

    public Response<String> updateOrderDetails(OrderModel orderModel,String oauthIdRh,String mobile){
        return orderDao.updateOrderDetails(orderModel,oauthIdRh,mobile);
    }

    public Response<String> updateOrderStatus(OrderModel orderModel,String oauthIdRH,String mobile){
        return orderDao.updateOrderStatus(orderModel,oauthIdRH,mobile);
    }
}
