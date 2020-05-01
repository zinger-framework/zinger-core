package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.OrderItemListModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import com.food.ordering.zinger.model.notification.NotificationModel;

public interface NotifyDao {

    Response<String> notifyInvitation(UserShopModel userShopModel);

    Response<String> sendGlobalNotification(NotificationModel notificationModel);

    void notifyOrderStatusToSeller(Response<OrderItemListModel> response);

    void notifyOrderStatus(Response<OrderItemListModel> response);
}
