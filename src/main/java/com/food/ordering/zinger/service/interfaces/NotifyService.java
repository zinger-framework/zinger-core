package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.NotificationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;

public interface NotifyService {
    Response<String> notifyInvitation(UserShopModel userShopModel);

    Response<String> sendUrlNotification(NotificationModel notificationModel);

    Response<String> sendNewArrivalNotification(NotificationModel notificationModel);
}
