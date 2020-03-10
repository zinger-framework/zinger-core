package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.model.OrderItemListModel;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.service.OrderService;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping(value = "")
    public Response<String> insertOrder(@RequestBody OrderItemListModel orderItemList, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.insertOrder(orderItemList, oauthId, mobile, role);
    }

    @GetMapping(value = "/customer/{mobile}/{pageNum}/{pageCount}")
    public Response<List<OrderModel>> getOrderByMobile(@PathVariable("mobile") String mobile, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageCount") Integer pageCount, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobileRh, @RequestHeader(value = UserColumn.role) String role){
       return orderService.getOrderByMobile(mobile, pageNum, pageCount, oauthId, mobileRh, role);
    }

    @GetMapping(value = "/seller/{shopId}/{pageNum}/{pageCount}")
    public Response<List<OrderModel>> getOrderByShopIdPagination(@PathVariable("shopId") Integer shopId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageCount") Integer pageCount, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role){
        return orderService.getOrderByShopIdPagination(shopId, pageNum, pageCount, oauthId, mobile, role);
    }

    @GetMapping(value = "/seller/{shopId}")
    public Response<List<OrderModel>> getOrderByShopId(@PathVariable("shopId") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role){
        return orderService.getOrderByShopId(shopId, oauthId, mobile, role);
    }

    @PatchMapping(value = "")
    public Response<String> updateOrder(@RequestBody OrderModel orderModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.updateOrder(orderModel, oauthId, mobile, role);
    }

    @PatchMapping(value = "/status")
    public Response<String> updateOrderStatus(@RequestBody OrderModel orderModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.updateOrderStatus(orderModel, oauthId, mobile, role);
    }
}
