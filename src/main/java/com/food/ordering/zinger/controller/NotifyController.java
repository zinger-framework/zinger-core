package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.dao.interfaces.NotifyDao;
import com.food.ordering.zinger.model.NotificationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import com.food.ordering.zinger.notification.PushNotification;
import com.food.ordering.zinger.service.interfaces.NotifyService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.food.ordering.zinger.constant.ApiConfig.NotifyApi.*;

@RestController
@RequestMapping(BASE_URL)
public class NotifyController {

    @Autowired
    NotifyService notifyService;


    @PostMapping(value = inviteSeller)
    public Response<String> notifyInvitation(@RequestBody UserShopModel userShopModel) {
        return notifyService.notifyInvitation(userShopModel);
    }


    @PostMapping(value = notifyUrl)
    public Response<String> notifyNewUrl(@RequestBody NotificationModel notificationModel){
        return notifyService.sendUrlNotification(notificationModel);
    }


    @PostMapping(value = notifyNewArrival)
    public Response<String> notifyNewArrival(@RequestBody NotificationModel notificationModel){
        return notifyService.sendNewArrivalNotification(notificationModel);
    }
}
