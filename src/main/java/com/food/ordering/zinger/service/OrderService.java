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

    public Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.insertOrder(orderItemListModel, requestHeaderModel);
    }

    public Response<String> placeOrder(Integer orderId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.placeOrder(orderId, requestHeaderModel);
    }

    public Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.getOrderByUserId(userId, pageNum, pageCount, requestHeaderModel);
    }

    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount, requestHeaderModel);
    }

    public Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.getOrderByShopId(shopId, requestHeaderModel);
    }

    public Response<TransactionModel> getOrderById(Integer id, String oauthId, Integer idRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, idRh, role);
        return orderDao.getOrderById(id, requestHeaderModel);
    }

    public Response<String> updateOrderRating(OrderModel orderModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.updateOrderRating(orderModel, requestHeaderModel);
    }

    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return orderDao.updateOrderStatus(orderModel, requestHeaderModel);
    }
}
