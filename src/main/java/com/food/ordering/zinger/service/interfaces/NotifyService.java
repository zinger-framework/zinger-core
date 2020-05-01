package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import com.food.ordering.zinger.model.notification.NotificationModel;

public interface NotifyService {
    Response<String> notifyInvitation(UserShopModel userShopModel);

    Response<String> sendGlobalNotification(NotificationModel notificationModel);
}
