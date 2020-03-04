package com.food.ordering.ssn.model;

import java.util.List;

public class OrderItemListModel {

    OrderModel orderModel;
    List<OrderItemModel> orderItemsList;

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public List<OrderItemModel> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItemModel> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }
}
