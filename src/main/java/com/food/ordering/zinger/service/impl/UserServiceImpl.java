package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.UserDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserPlaceModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.model.notification.UserNotificationModel;
import com.food.ordering.zinger.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;


    @Override
    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        Response<UserPlaceModel> response = userDao.loginRegisterCustomer(user);
        return response;
    }

    @Override
    public Response<UserShopListModel> verifySeller(UserModel user) {
        Response<UserShopListModel> response = userDao.verifySeller(user);
        return response;
    }


    /**************************************************/

    @Override
    public Response<String> updateUser(UserModel userModel) {
        Response<String> response = new Response<>();
        try {
            response = userDao.updateUser(userModel);
        } catch (Exception e) {
            response.setCode(ErrorLog.UDNU1157);
            response.setMessage(ErrorLog.UserDetailNotUpdated);
        }
        return response;
    }

    @Override
    public Response<String> updateUserNotificationToken(UserNotificationModel userNotificationModel) {
        Response<String> response = userDao.updateUserNotificationToken(userNotificationModel);
        return response;
    }

    @Override
    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel) {
        Response<String> response = new Response<>();
        try {
            response = userDao.updateUserPlaceData(userPlaceModel);
        } catch (Exception e) {
            response.setCode(ErrorLog.UDNU1157);
            response.setMessage(ErrorLog.UserDetailNotUpdated);
        }
        return response;
    }

}
