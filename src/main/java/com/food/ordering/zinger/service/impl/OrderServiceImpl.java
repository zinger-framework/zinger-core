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
        auditLogDao.insertOrderLog(new OrderLogModel(response, orderItemListModel.getTransactionModel().getOrderModel().getId(), orderItemListModel.toString()));
        return response;
    }

    @Override
    public Response<String> placeOrder(Integer orderId) {
        Response<String> response = orderDao.placeOrder(orderId);
        auditLogDao.insertOrderLog(new OrderLogModel(response, null, orderId.toString()));
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = orderDao.getOrderByUserId(userId, pageNum, pageCount);
        auditLogDao.insertOrderLog(new OrderLogModel(response, null, userId + " - " + pageNum));
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderBySearchQuery(Integer shopId, String searchItem, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = orderDao.getOrderBySearchQuery(shopId, searchItem, pageNum, pageCount);
        auditLogDao.insertOrderLog(new OrderLogModel(response, null, shopId + " - " + searchItem + " - " + pageNum));
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount);
        auditLogDao.insertOrderLog(new OrderLogModel(response, null, shopId + " - " + pageNum));
        return orderDao.getOrderByShopIdPagination(shopId, pageNum, pageCount);
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId) {
        Response<List<OrderItemListModel>> response = orderDao.getOrderByShopId(shopId);
        auditLogDao.insertOrderLog(new OrderLogModel(response, shopId, shopId.toString()));
        return response;
    }

    @Override
    public Response<TransactionModel> getOrderById(Integer id) {
        return orderDao.getOrderById(id);
    }

    @Override
    public Response<String> updateOrderRating(OrderModel orderModel) {
        Response<String> response = orderDao.updateOrderRating(orderModel);
        auditLogDao.insertOrderLog(new OrderLogModel(response, orderModel.getId(), orderModel.toString()));
        return response;
    }

    @Override
    public Response<String> updateOrderStatus(OrderModel orderModel) {
        Response<String> response = orderDao.updateOrderStatus(orderModel);
        auditLogDao.insertOrderLog(new OrderLogModel(response, orderModel.getId(), orderModel.toString()));
        return response;
    }
}
