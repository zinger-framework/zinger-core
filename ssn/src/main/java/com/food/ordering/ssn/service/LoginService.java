package com.food.ordering.ssn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.food.ordering.ssn.dao.LoginDao;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.utils.Response;

@Service
public class LoginService {

	@Autowired
	LoginDao loginDao;
	
	public Response<?> insertUser(UserModel user) {
		return loginDao.insertUser(user);
	}

	public Response<List<UserModel>> getAllUser(String oauthId) {
		return loginDao.getAllUser(oauthId);
	}
	
	public Response<UserModel> getUserByOauthId(String oauthId,String oauthIdRh) {
		return loginDao.getUserByOauthId(oauthId,oauthIdRh);
    }
	
	public Response<String> updateUserByOauthId(UserModel user,String oauthId, Integer id) {
		return loginDao.updateUserByOauthId(user,oauthId,id);
	}
	
	public Response<UserModel> deleteUserByOauthId(String oauthId,String oauthIdRh) {
		return loginDao.deleteUserByOauthId(oauthId,oauthIdRh);
	}

}
