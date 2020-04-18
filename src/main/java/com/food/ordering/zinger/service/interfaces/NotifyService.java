package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.impl.NotifyDaoImpl;
import com.food.ordering.zinger.dao.impl.UserDaoImpl;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserInviteModel;
import com.food.ordering.zinger.model.UserShopModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface NotifyService {
    Response<String> notifyInvitation(UserShopModel userShopModel);
}
