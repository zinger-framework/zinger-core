package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.AuditLogDao;
import com.food.ordering.zinger.dao.interfaces.UserDao;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.UserLogModel;
import com.food.ordering.zinger.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuditLogDao auditLogDao;

    @Override
    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        Response<UserPlaceModel> response = userDao.loginRegisterCustomer(user);
        auditLogDao.insertUserLog(new UserLogModel(response, null, user.toString()));
        return response;
    }

    @Override
    public Response<UserShopListModel> verifySeller(UserModel user) {
        Response<UserShopListModel> response = userDao.verifySeller(user);
        auditLogDao.insertUserLog(new UserLogModel(response, null, user.toString()));
        return response;
    }

    @Override
    public Response<String> inviteSeller(UserShopModel userShopModel) {
        Response<String> response = userDao.inviteSeller(userShopModel);
        auditLogDao.insertUserLog(new UserLogModel(response, null, userShopModel.toString()));
        return response;
    }

    @Override
    public Response<UserShopListModel> acceptInvite(UserShopModel userShopModel) {
        Response<UserShopListModel> response = userDao.acceptInvite(userShopModel);
        auditLogDao.insertUserLog(new UserLogModel(response, null, userShopModel.getUserModel().getMobile()));
        return response;
    }

    /**************************************************/

    @Override
    public Response<List<UserModel>> getSellerByShopId(Integer shopId) {
        Response<List<UserModel>> response = userDao.getSellerByShopId(shopId);
        auditLogDao.insertUserLog(new UserLogModel(response, null, shopId.toString()));
        return response;
    }

    @Override
    public Response<UserModel> verifyInvite(Integer shopId, String mobile) {
        Response<UserModel> response = userDao.verifyInvite(shopId, mobile);
        auditLogDao.insertUserLog(new UserLogModel(response, null, shopId.toString()));
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
        auditLogDao.insertUserLog(new UserLogModel(response, userModel.getId(), userModel.toString()));
        return response;
    }

    @Override
    public Response<String> updateUserNotificationToken(UserNotificationModel userNotificationModel) {
        Response<String> response = userDao.updateUserNotificationToken(userNotificationModel);
        auditLogDao.insertUserLog(new UserLogModel(response, userNotificationModel.getId(), userNotificationModel.toString()));
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
        auditLogDao.insertUserLog(new UserLogModel(response, userPlaceModel.getUserModel().getId(), userPlaceModel.toString()));
        return response;
    }

    @Override
    public Response<String> deleteSeller(Integer shopId, Integer userId) {
        Response<String> response = userDao.deleteSeller(shopId, userId);
        auditLogDao.insertUserLog(new UserLogModel(response, userId, null));
        return response;
    }

    @Override
    public Response<String> deleteInvite(UserShopModel userShopModel) {
        Response<String> response = userDao.deleteInvite(userShopModel);
        auditLogDao.insertUserLog(new UserLogModel(response, null, userShopModel.toString()));
        return response;
    }
}
