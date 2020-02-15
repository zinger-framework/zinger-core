package com.food.ordering.ssn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.service.LoginService;

@RestController
@RequestMapping("/user")
public class LoginController {
	
	@Autowired
	LoginService loginService;
	
	@GetMapping(value = "")
    public List<UserModel> getAllUser() {
        return loginService.getAllUser();
    }
	
//	@GetMapping(value = "/secured")
//    public List<UserModel> getAllUsersSecured(@RequestHeader(value="auth_code") String authCode, @RequestHeader(value="access_token") String accessToken) {
//        return loginService.getAllUsers();
//    }
	
	@GetMapping(value = "/{id}")
    public UserModel getUserById(@PathVariable("id") String id) {
        return getAllUser().get(0);
    }
	
	@PostMapping(value = "")
    public UserModel insertUser(@RequestBody UserModel user) {
		System.out.println(user.getName());
		return getAllUser().get(0);
    }
	
	@PatchMapping(value = "/{id}")
    public UserModel updateUserById(@PathVariable("id") String id, @RequestBody UserModel user) {
        return getAllUser().get(0);
    }
	
	@DeleteMapping(value = "/{id}")
    public Integer deleteUserById(@PathVariable("id") Integer id) {
    	return loginService.deleteUser(id);
    }
}
