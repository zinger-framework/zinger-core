package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.*;

public interface NotifyDao {

    Response<String> notifyInvitation(UserShopModel userShopModel);

    void notifyNewOrder(Response<OrderItemListModel> response);

    void notifyUpdateOrder(Response<OrderItemListModel> response);

    void notifyCancelOrderByUser(Response<OrderItemListModel> response);

    Response<String> notifyWebView(NotificationModel notificationModel);

    Response<String> notifyNewArrival(NotificationModel notificationModel);

}
