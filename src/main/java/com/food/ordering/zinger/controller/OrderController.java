package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.OrderItemListModel;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;
import com.food.ordering.zinger.service.OrderService;
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
    public Response<String> insertOrder(@RequestBody OrderItemListModel orderItemList, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.insertOrder(orderItemList, oauthId, mobile, role);
    }

    @PostMapping(value = acceptOrder)
    public Response<String> acceptOrder(@PathVariable("orderId") String orderId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.acceptOrder(orderId, oauthId, mobile, role);
    }

    @GetMapping(value = getOrderByMobile)
    public Response<List<OrderItemListModel>> getOrderByMobile(@PathVariable("mobile") String mobile, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageCount") Integer pageCount, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobileRh, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.getOrderByMobile(mobile, pageNum, pageCount, oauthId, mobileRh, role);
    }

    @GetMapping(value = getOrderByShopIdPagination)
    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(@PathVariable("shopId") Integer shopId, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageCount") Integer pageCount, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.getOrderByShopIdPagination(shopId, pageNum, pageCount, oauthId, mobile, role);
    }

    @GetMapping(value = getOrderByShopId)
    public Response<List<OrderItemListModel>> getOrderByShopId(@PathVariable("shopId") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.getOrderByShopId(shopId, oauthId, mobile, role);
    }

    @GetMapping(value = getOrderById)
    public Response<TransactionModel> getOrderById(@PathVariable("id") String id, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.getOrderById(id, oauthId, mobile, role);
    }

    @PatchMapping(value = updateOrderRating)
    public Response<String> updateOrderRating(@RequestBody OrderModel orderModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.updateOrderRating(orderModel, oauthId, mobile, role);
    }

    @PatchMapping(value = updateOrderKey)
    public Response<String> updateOrderKey(@RequestBody OrderModel orderModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.updateOrderKey(orderModel, oauthId, mobile, role);
    }

    @PatchMapping(value = updateOrderStatus)
    public Response<String> updateOrderStatus(@RequestBody OrderModel orderModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return orderService.updateOrderStatus(orderModel, oauthId, mobile, role);
    }
}
