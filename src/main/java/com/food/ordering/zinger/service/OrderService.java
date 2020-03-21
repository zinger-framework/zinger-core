package com.food.ordering.zinger.service;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.dao.OrderDao;
import com.food.ordering.zinger.model.OrderItemListModel;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.ResponseHeaderModel;
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
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.insertOrder(orderItemListModel, responseHeader);
    }

    public Response<String> verifyOrder(OrderItemListModel orderItemListModel, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.verifyOrder(orderItemListModel, responseHeader);
    }

    public Response<List<OrderItemListModel>> getOrderByMobile(String mobile, Integer pageNum, Integer pageCount, String oauthId, String mobileRh, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderByMobile(mobile, pageNum, pageCount, responseHeader);
    }

    public Response<List<OrderModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount, responseHeader);
    }

    public Response<List<OrderModel>> getOrderByShopId(Integer shopId, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderByShopId(shopId, responseHeader);
    }

    public Response<OrderModel> getOrderById(String id, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.getOrderById(id, responseHeader);
    }

    public Response<String> updateOrder(OrderModel orderModel, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.updateOrder(orderModel, responseHeader);
    }

    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthId, String mobile, String role) {
        ResponseHeaderModel responseHeader = new ResponseHeaderModel(oauthId, mobile, role);
        return orderDao.updateOrderStatus(orderModel, responseHeader);
    }
}
