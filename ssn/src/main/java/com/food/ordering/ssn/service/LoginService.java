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
	
	public Response<UserModel> insertUser(UserModel user, String oauthId, String accessToken) {
		return loginDao.insertUser(user, oauthId, accessToken);
	}

	public Response<List<UserModel>> getAllUser(String oauthId, String accessToken) {
		return loginDao.getAllUser(oauthId,accessToken);
	}
	
	public Response<UserModel> getUserByOauthId(String oauthId,String oauthIdRh, String accessToken) {
		return loginDao.getUserByOauthId(oauthId,oauthIdRh,accessToken);
    }
	
	public Response<UserModel> updateUserByOauthId(UserModel user,String oauthId, String accessToken) {
		return loginDao.updateUserByOauthId(user,oauthId,accessToken);
	}
	
	public Response<UserModel> deleteUserByOauthId(String oauthId,String oauthIdRh, String accessToken) {
		return loginDao.deleteUserByOauthId(oauthId,oauthIdRh,accessToken);
	}

	
//	public Response<UserModel> getUserByID(Integer id) {
//	return loginDao.getUserById(id);
//}

}
