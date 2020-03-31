package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.UserDao;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.UserCollegeModel;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public Response<UserCollegeModel> loginRegisterCustomer(UserModel user) {
        return userDao.loginRegisterCustomer(user);
    }

    public Response<UserShopListModel> verifySeller(UserModel user) {
        return userDao.verifySeller(user);
    }

    public Response<String> insertSeller(String mobile, Integer shopId, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.insertSeller(mobile, shopId, requestHeaderModel);
    }

    public Response<List<UserModel>> getSellerByShopId(Integer shopId, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.getSellerByShopId(shopId, requestHeaderModel);
    }

    /**************************************************/

    public Response<String> updateUser(UserModel userModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return userDao.updateUser(userModel, requestHeaderModel);
    }

    public Response<String> updateUserCollegeData(UserCollegeModel userCollegeModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return userDao.updateUserCollegeData(userCollegeModel, requestHeaderModel);
    }

    public Response<String> deleteSeller(Integer shopId, String mobile, String oauthId, String mobileRh, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobileRh, role);
        return userDao.deleteSeller(shopId, mobile, requestHeaderModel);
    }
}
