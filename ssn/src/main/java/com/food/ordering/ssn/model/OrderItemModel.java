package com.food.ordering.ssn.model;

public class OrderItemModel {
    private OrderModel orderModel;
    private ItemModel itemModel;
    private Integer quantity;
    private Double price;

    public OrderItemModel() {
        orderModel = new OrderModel();
        itemModel = new ItemModel();
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public ItemModel getItemModel() {
        return itemModel;
    }

    public void setItemModel(ItemModel itemModel) {
        this.itemModel = itemModel;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItemModel{" +
                "orderModel=" + orderModel +
                ", itemModel=" + itemModel +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
