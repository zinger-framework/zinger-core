package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.NotifyDao;
import com.food.ordering.zinger.dao.interfaces.UserDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.notification.NotificationModel;
import com.food.ordering.zinger.service.interfaces.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    NotifyDao notifyDao;

    @Autowired
    UserDao userDao;

    @Override
    public Response<String> sendGlobalNotification(NotificationModel notificationModel) {
        return notifyDao.sendGlobalNotification(notificationModel);
    }
}
