package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.UserDao;
import com.food.ordering.zinger.model.UserCollegeModel;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public Response<UserCollegeModel> insertCustomer(UserModel user) {
        return userDao.insertCustomer(user);
    }

    public Response<UserShopListModel> insertSeller(UserModel user) {
        return userDao.insertSeller(user);
    }

    /**************************************************/

    public Response<String> updateUser(UserModel userModel, String oauthId, String mobile, String role) {
        return userDao.updateUser(userModel, oauthId, mobile, role);
    }

    public Response<String> updateUserCollegeData(UserCollegeModel userCollegeModel, String oauthId, String mobile, String role) {
        return userDao.updateUserCollegeData(userCollegeModel, oauthId, mobile, role);
    }
}
