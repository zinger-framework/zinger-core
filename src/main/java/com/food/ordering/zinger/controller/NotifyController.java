package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import com.food.ordering.zinger.model.notification.NotificationModel;
import com.food.ordering.zinger.service.interfaces.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.food.ordering.zinger.constant.ApiConfig.NotifyApi.*;

@RestController
@RequestMapping(BASE_URL)
public class NotifyController {

    @Autowired
    NotifyService notifyService;


    @PostMapping(value = notifyUrl)
    public Response<String> notifyNewUrl(@RequestBody NotificationModel notificationModel) {
        return notifyService.sendGlobalNotification(notificationModel);
    }

    @PostMapping(value = notifyNewArrival)
    public Response<String> notifyNewArrival(@RequestBody NotificationModel notificationModel) {
        return notifyService.sendGlobalNotification(notificationModel);
    }
}
