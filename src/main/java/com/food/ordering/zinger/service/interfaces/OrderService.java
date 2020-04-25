package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.dao.impl.OrderDaoImpl;
import com.food.ordering.zinger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {

    Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel);

    Response<String> placeOrder(Integer orderId);

    Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount);

    Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount);

    Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId);

    Response<TransactionModel> getOrderById(Integer id);

    Response<String> updateOrderRating(OrderModel orderModel);

    Response<String> updateOrderStatus(OrderModel orderModel);
}
