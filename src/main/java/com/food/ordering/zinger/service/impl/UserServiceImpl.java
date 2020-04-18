package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.*;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        return userDao.loginRegisterCustomer(user);
    }

    @Override
    public Response<UserShopListModel> verifySeller(UserModel user) {
        return userDao.verifySeller(user);
    }

    @Override
    public Response<String> inviteSeller(UserShopModel userShopModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.inviteSeller(userShopModel, requestHeaderModel);
    }

    @Override
    public Response<String> acceptInvite(UserShopModel userShopModel) {
        return userDao.acceptInvite(userShopModel);
    }

    /**************************************************/

    @Override
    public Response<List<UserModel>> getSellerByShopId(Integer shopId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.getSellerByShopId(shopId, requestHeaderModel);
    }

    @Override
    public Response<UserInviteModel> verifyInvite(Integer shopId, String mobile) {
        return userDao.verifyInvite(shopId, mobile);
    }

    /**************************************************/

    @Override
    public Response<String> updateUser(UserModel userModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.updateUser(userModel, requestHeaderModel);
    }

    @Override
    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.updateUserPlaceData(userPlaceModel, requestHeaderModel);
    }

    @Override
    public Response<String> deleteSeller(Integer shopId, Integer userId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.deleteSeller(shopId, userId, requestHeaderModel);
    }

    @Override
    public Response<String> deleteInvite(UserShopModel userShopModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.deleteInvite(userShopModel, requestHeaderModel);
    }
}
