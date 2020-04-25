package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.impl.AuditLogDaoImpl;
import com.food.ordering.zinger.dao.impl.InterceptorDaoImpl;
import com.food.ordering.zinger.dao.interfaces.*;
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
    InterceptorDao interceptorDao;

    @Autowired
    AuditLogDao auditLogDao;

    @Override
    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        return userDao.loginRegisterCustomer(user);
    }

    @Override
    public Response<UserShopListModel> verifySeller(UserModel user) {
        return userDao.verifySeller(user);
    }

    @Override
    public Response<String> inviteSeller(UserShopModel userShopModel) {
        Response<String> response = new Response<>();
        auditLogDao.insertUserLog(new UserLogModel(response, null, userShopModel.toString()));
        return response;
    }

    @Override
    public Response<UserShopListModel> acceptInvite(UserShopModel userShopModel) {
        return userDao.acceptInvite(userShopModel);
    }

    /**************************************************/

    @Override
    public Response<List<UserModel>> getSellerByShopId(Integer shopId) {
        return userDao.getSellerByShopId(shopId);
    }

    @Override
    public Response<UserInviteModel> verifyInvite(Integer shopId, String mobile) {
        return userDao.verifyInvite(shopId, mobile);
    }

    /**************************************************/

    @Override
    public Response<String> updateUser(UserModel userModel) {
        return userDao.updateUser(userModel);
    }

    @Override
    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel) {
        Response<String> response = updateUserPlaceData(userPlaceModel);
        auditLogDao.insertUserLog(new UserLogModel(response, userPlaceModel.getUserModel().getId(), userPlaceModel.toString()));
        return response;
    }

    @Override
    public Response<String> deleteSeller(Integer shopId, Integer userId) {
        return userDao.deleteSeller(shopId, userId);
    }

    @Override
    public Response<String> deleteInvite(UserShopModel userShopModel) {
        return userDao.deleteInvite(userShopModel);
    }
}
