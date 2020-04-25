package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.OrderDao;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Override
    public Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel) {
        return orderDao.insertOrder(orderItemListModel);
    }

    @Override
    public Response<String> placeOrder(Integer orderId) {
        return orderDao.placeOrder(orderId);
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount) {
        return orderDao.getOrderByUserId(userId, pageNum, pageCount);
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByUserNameOrOrderId(String searchItem, Integer pageNum, Integer pageCount) {
        return orderDao.getOrderByUserNameOrOrderId(searchItem, pageNum, pageCount);
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount) {
        return orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount);
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId) {
        return orderDao.getOrderByShopId(shopId);
    }

    @Override
    public Response<TransactionModel> getOrderById(Integer id) {
        return orderDao.getOrderById(id);
    }

    @Override
    public Response<String> updateOrderRating(OrderModel orderModel) {
        return orderDao.updateOrderRating(orderModel);
    }

    @Override
    public Response<String> updateOrderStatus(OrderModel orderModel) {
        return orderDao.updateOrderStatus(orderModel);
    }
}
