package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.NotificationModel;
import com.food.ordering.zinger.model.OrderItemListModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;

public interface NotifyDao {

    Response<String> notifyInvitation(UserShopModel userShopModel);

    void notifyNewOrder(Response<OrderItemListModel> response);

    void notifyUpdateOrder(Response<OrderItemListModel> response);

    void notifyCancelOrderByUser(Response<OrderItemListModel> response);

    Response<String> notifyWebView(NotificationModel notificationModel);

    Response<String> notifyNewArrival(NotificationModel notificationModel);
}
