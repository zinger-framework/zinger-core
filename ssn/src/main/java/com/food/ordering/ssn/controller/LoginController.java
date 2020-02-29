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
import com.food.ordering.ssn.utils.Response;

@RestController
@RequestMapping("/user")
public class LoginController {

	@Autowired
	LoginService loginService;

	@PostMapping(value = "/insertUser")
	public Response<?> insertUser(@RequestBody UserModel user) {
		return loginService.insertUser(user);
	}

	@GetMapping(value = "")
	public Response<List<UserModel>> getAllUser(@RequestHeader(value = "oauth_id") String oauthId) {
		return loginService.getAllUser(oauthId);
	}

	@GetMapping(value = "/{oauth_id}")
	public Response<UserModel> getUserByOauthId(@PathVariable("oauth_id") String oauthId,
			@RequestHeader(value = "oauth_id") String oauthIdRh) {
		return loginService.getUserByOauthId(oauthId, oauthIdRh);
	}
 
	@PatchMapping(value = "/updateUserDetails")
	public Response<String> updateUserByOauthId(@RequestBody UserModel user,
			@RequestHeader(value = "oauth_id") String oauthId, @RequestHeader(value = "id") Integer id) {
		return loginService.updateUserByOauthId(user, oauthId,id);
	}

	@DeleteMapping(value = "/{oauth_id}")
	public Response<UserModel> deleteUserByOauthId(@PathVariable("oauth_id") String oauthId,
			@RequestHeader(value = "oauth_id") String oauthIdRh) {
		return loginService.deleteUserByOauthId(oauthId, oauthIdRh);
	}

//	@GetMapping(value = "/id/{id}")
//  public Response<UserModel> getUserById(@PathVariable("id") Integer id) {
//		return loginService.getUserByID(id);
//  }
}
