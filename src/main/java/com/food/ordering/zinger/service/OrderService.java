package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.OrderDao;
import com.food.ordering.zinger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public Response<String> insertOrder(OrderItemListModel orderItemListModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.insertOrder(orderItemListModel, requestHeaderModel);
    }

    public Response<String> placeOrder(String orderId, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.placeOrder(orderId, requestHeaderModel);
    }

    public Response<List<OrderItemListModel>> getOrderByMobile(String mobile, Integer pageNum, Integer pageCount, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return orderDao.getOrderByMobile(mobile, pageNum, pageCount, requestHeaderModel);
    }

    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount, requestHeaderModel);
    }

    public Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderByShopId(shopId, requestHeaderModel);
    }

    public Response<TransactionModel> getOrderById(String id, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderById(id, requestHeaderModel);
    }

    public Response<String> updateOrderRating(OrderModel orderModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.updateOrderRating(orderModel, requestHeaderModel);
    }

    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return orderDao.updateOrderStatus(orderModel, requestHeaderModel);
    }
}
