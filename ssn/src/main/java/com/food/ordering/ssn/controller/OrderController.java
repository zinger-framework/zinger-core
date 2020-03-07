package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.OrderColumn;
import com.food.ordering.ssn.model.OrderItemListModel;
import com.food.ordering.ssn.model.OrderModel;
import com.food.ordering.ssn.service.OrderService;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping
    public Response<String> insertOrderDetails(@RequestBody OrderItemListModel orderItemList, @RequestHeader(value = OrderColumn.oauthId) String oauthIdRh, @RequestHeader(value = OrderColumn.mobile) String mobile) {
        return orderService.insertOrderDetails(orderItemList, oauthIdRh, mobile);
    }

    @PatchMapping
    public Response<String> updateOrderDetails(@RequestBody OrderModel orderModel, @RequestHeader(value = OrderColumn.oauthId) String oauthIdRh, @RequestHeader(value = OrderColumn.mobile) String mobile) {
        return orderService.updateOrderDetails(orderModel, oauthIdRh, mobile);
    }

    @PatchMapping(value = "/status")
    public Response<String> updateOrderStatus(OrderModel orderModel, String oauthIdRH, String mobile) {
        return orderService.updateOrderStatus(orderModel, oauthIdRH, mobile);
    }


}
