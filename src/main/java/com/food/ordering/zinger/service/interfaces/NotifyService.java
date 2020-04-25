package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;

public interface NotifyService {
    Response<String> notifyInvitation(UserShopModel userShopModel);
}
