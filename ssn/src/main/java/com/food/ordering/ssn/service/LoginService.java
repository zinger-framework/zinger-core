package com.food.ordering.ssn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.food.ordering.ssn.dao.LoginDao;
import com.food.ordering.ssn.model.UserModel;

@Service
public class LoginService {
	
	@Autowired
	LoginDao loginDao;

    public List<UserModel> getAllUser() {
        return loginDao.getAllUser();
    }
    
    public Integer deleteUser(Integer id) {
    	return loginDao.deleteUser(id);
    }
}
