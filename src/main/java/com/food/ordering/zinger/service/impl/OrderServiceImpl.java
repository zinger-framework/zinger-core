package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.AuditLogDao;
import com.food.ordering.zinger.dao.interfaces.OrderDao;
import com.food.ordering.zinger.exception.GenericException;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.OrderLogModel;
import com.food.ordering.zinger.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    AuditLogDao auditLogDao;

    @Override
    public Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel) {
        Response<TransactionTokenModel> response = new Response<>();
        try {
            response = orderDao.insertOrder(orderItemListModel);
        } catch (GenericException e) {
            response = e.getResponse();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        auditLogDao.insertOrderLog(new OrderLogModel(response, orderItemListModel.getTransactionModel().getOrderModel().getId(), orderItemListModel.toString(), response.priorityGet()));
        return response;
    }

    @Override
    public Response<String> placeOrder(Integer orderId) {
        return orderDao.placeOrder(orderId);
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = orderDao.getOrderByUserId(userId, pageNum, pageCount);
        auditLogDao.insertOrderLog(new OrderLogModel(response, null, userId + " - " + pageNum, response.priorityGet()));
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderBySearchQuery(Integer shopId, String searchItem, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = orderDao.getOrderBySearchQuery(shopId, searchItem, pageNum, pageCount);
        auditLogDao.insertOrderLog(new OrderLogModel(response, null, shopId + " - " + searchItem + " - " + pageNum, response.priorityGet()));
        return response;
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
