package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.dao.impl.OrderDaoImpl;
import com.food.ordering.zinger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {

    Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel, String oauthId, Integer id, String role);

    Response<String> placeOrder(Integer orderId, String oauthId, Integer id, String role);

    Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount, String oauthId, Integer id, String role);

    Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, String oauthId, Integer id, String role);

    Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId, String oauthId, Integer id, String role);

    Response<TransactionModel> getOrderById(Integer id, String oauthId, Integer idRh, String role);

    Response<String> updateOrderRating(OrderModel orderModel, String oauthId, Integer id, String role);

    Response<String> updateOrderStatus(OrderModel orderModel, String oauthId, Integer id, String role);
}
