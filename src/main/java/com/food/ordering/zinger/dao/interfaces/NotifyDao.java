package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;

public interface NotifyDao {
    Response<String> notifyInvitation(UserShopModel userShopModel);
}
