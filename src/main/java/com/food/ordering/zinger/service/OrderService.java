package com.food.ordering.zinger.service;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.dao.OrderDao;
import com.food.ordering.zinger.model.OrderItemListModel;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public Response<String> insertOrder(OrderItemListModel orderItemListModel, String oauthId, String mobile, String role) {
        return orderDao.insertOrder(orderItemListModel, oauthId, mobile, role);
    }

    public Response<List<OrderModel>> getOrderByMobile(String mobile, Integer pageNum, Integer pageCount, String oauthId, String mobileRh, String role){
        return orderDao.getOrderByMobile(mobile, pageNum, pageCount, oauthId, mobileRh, role);
    }

    public Response<List<OrderModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, String oauthId, String mobile, String role){
        return orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount, oauthId, mobile, role);
    }

    public Response<List<OrderModel>> getOrderByShopId(Integer shopId, String oauthId, String mobile, String role){
        return orderDao.getOrderByShopId(shopId, oauthId, mobile, role);
    }

    public Response<String> updateOrder(OrderModel orderModel, String oauthId, String mobile, String role) {
        return orderDao.updateOrder(orderModel, oauthId, mobile, role);
    }

    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthId, String mobile, String role) {
        return orderDao.updateOrderStatus(orderModel, oauthId, mobile, role);
    }
}
