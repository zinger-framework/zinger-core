package com.food.ordering.ssn.controller;

import java.util.ArrayList;
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
import com.food.ordering.ssn.utils.Utils;

@RestController
@RequestMapping("/user")
public class LoginController {
	
	@Autowired
	LoginService loginService;
	
	@PostMapping(value = "/insertUser")
    public Response<UserModel> insertUser(@RequestBody UserModel user, @RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
		return loginService.insertUser(user, oauthId, accessToken);
    }
	
	@GetMapping(value = "")
    public Response<List<UserModel>> getAllUser(@RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
        return loginService.getAllUser(oauthId,accessToken);
    }
	
	@GetMapping(value = "/{oauth_id}")
    public Response<UserModel> getUserByOauthId(@PathVariable("oauth_id") String oauthId,@RequestHeader(value="oauth_id") String oauthIdRh, @RequestHeader(value="access_token") String accessToken) {
		return loginService.getUserByOauthId(oauthId,oauthIdRh,accessToken);
    }
	
	@PatchMapping(value = "")
    public Response<UserModel> updateUserByOauthId(@RequestBody UserModel user,@RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
		return loginService.updateUserByOauthId(user,oauthId,accessToken);
	}
	
	@DeleteMapping(value = "/{oauth_id}")
    public Response<UserModel> deleteUserByOauthId(@PathVariable("oauth_id") String oauthId,@RequestHeader(value="oauth_id") String oauthIdRh, @RequestHeader(value="access_token") String accessToken) {
		return loginService.deleteUserByOauthId(oauthId,oauthIdRh,accessToken);
    }
	
//	@GetMapping(value = "/id/{id}")
//  public Response<UserModel> getUserById(@PathVariable("id") Integer id) {
//		return loginService.getUserByID(id);
//  }
}
