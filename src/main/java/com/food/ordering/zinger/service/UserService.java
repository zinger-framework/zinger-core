package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.UserDao;
import com.food.ordering.zinger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        return userDao.loginRegisterCustomer(user);
    }

    public Response<UserShopListModel> verifySeller(UserModel user) {
        return userDao.verifySeller(user);
    }

    public Response<String> inviteSeller(UserShopModel userShopModel, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.inviteSeller(userShopModel, requestHeaderModel);
    }

    public Response<String> acceptInvite(UserShopModel userShopModel) {
        return userDao.acceptInvite(userShopModel);
    }

    /**************************************************/

    public Response<List<UserModel>> getSellerByShopId(Integer shopId, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.getSellerByShopId(shopId, requestHeaderModel);
    }

    public Response<UserInviteModel> verifyInvite(Integer shopId, String mobile) {
        return userDao.verifyInvite(shopId, mobile);
    }

    /**************************************************/

    public Response<String> updateUser(UserModel userModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return userDao.updateUser(userModel, requestHeaderModel);
    }

    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return userDao.updateUserPlaceData(userPlaceModel, requestHeaderModel);
    }

    public Response<String> deleteSeller(Integer shopId, Integer userId, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.deleteSeller(shopId, userId, requestHeaderModel);
    }

    public Response<String> deleteInvite(UserShopModel userShopModel, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.deleteInvite(userShopModel, requestHeaderModel);
    }
}
