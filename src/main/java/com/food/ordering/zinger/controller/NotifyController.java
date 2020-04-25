package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import com.food.ordering.zinger.service.impl.NotifyServiceImpl;
import com.food.ordering.zinger.service.interfaces.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.food.ordering.zinger.constant.ApiConfig.NotifyApi.BASE_URL;
import static com.food.ordering.zinger.constant.ApiConfig.NotifyApi.inviteSeller;

@RestController
@RequestMapping(BASE_URL)
public class NotifyController {

    @Autowired
    NotifyService notifyService;

    @PostMapping(value = inviteSeller)
    public Response<String> notifyInvitation(@RequestBody UserShopModel userShopModel) {
        return notifyService.notifyInvitation(userShopModel);
    }
}
