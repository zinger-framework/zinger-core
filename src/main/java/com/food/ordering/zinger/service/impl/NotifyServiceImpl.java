package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.impl.NotifyDaoImpl;
import com.food.ordering.zinger.dao.impl.UserDaoImpl;
import com.food.ordering.zinger.dao.interfaces.NotifyDao;
import com.food.ordering.zinger.dao.interfaces.UserDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserInviteModel;
import com.food.ordering.zinger.model.UserShopModel;
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
    public Response<String> notifyInvitation(UserShopModel userShopModel) {
        Response<String> response = new Response<>();
        Response<UserInviteModel> inviteModelResponse = userDao.verifyInvite(userShopModel.getShopModel().getId(), userShopModel.getUserModel().getMobile());
        if (inviteModelResponse.getCode().equals(ErrorLog.CodeSuccess))
            return notifyDao.notifyInvitation(userShopModel);
        else {
            response.setCode(inviteModelResponse.getCode());
            response.setMessage(inviteModelResponse.getMessage());
        }
        return response;
    }
}
