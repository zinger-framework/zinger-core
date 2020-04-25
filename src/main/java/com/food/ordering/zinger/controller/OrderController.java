package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.food.ordering.zinger.constant.ApiConfig.OrderApi.*;

@RestController
@RequestMapping(BASE_URL)
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping(value = insertOrder)
    public Response<TransactionTokenModel> insertOrder(@RequestBody OrderItemListModel orderItemList) {
        return orderService.insertOrder(orderItemList);
    }

    @PostMapping(value = placeOrder)
    public Response<String> placeOrder(@PathVariable("orderId") Integer orderId) {
        return orderService.placeOrder(orderId);
    }

    @GetMapping(value = getOrderByUserId)
    public Response<List<OrderItemListModel>> getOrderByUserId(@PathVariable("userId") Integer userId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageCount") Integer pageCount) {
        return orderService.getOrderByUserId(userId, pageNum, pageCount);
    }

    @GetMapping(value = getOrderByShopIdPagination)
    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(@PathVariable("shopId") Integer shopId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageCount") Integer pageCount) {
        return orderService.getOrderByShopIdPagination(shopId, pageNum, pageCount);
    }

    @GetMapping(value = getOrderByShopId)
    public Response<List<OrderItemListModel>> getOrderByShopId(@PathVariable("shopId") Integer shopId) {
        return orderService.getOrderByShopId(shopId);
    }

    @GetMapping(value = getOrderById)
    public Response<TransactionModel> getOrderById(@PathVariable("id") Integer id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping(value = updateOrderRating)
    public Response<String> updateOrderRating(@RequestBody OrderModel orderModel) {
        return orderService.updateOrderRating(orderModel);
    }

    @PatchMapping(value = updateOrderStatus)
    public Response<String> updateOrderStatus(@RequestBody OrderModel orderModel) {
        return orderService.updateOrderStatus(orderModel);
    }
}
