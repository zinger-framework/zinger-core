package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.*;

import java.util.List;

public interface OrderDao {
    Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel);

    /**************************************************/

    Response<String> placeOrder(Integer orderId);

    /**************************************************/

    Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount);

    Response<List<OrderItemListModel>> getOrderByUserNameOrOrderId(String searchItem, Integer pageNum, Integer pageCount);

    Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount);

    Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId);

    Response<TransactionModel> getOrderById(Integer orderId);

    /**************************************************/

    Response<String> updateOrderRating(OrderModel orderModel);

    Response<String> updateOrderStatus(OrderModel orderModel);
}
