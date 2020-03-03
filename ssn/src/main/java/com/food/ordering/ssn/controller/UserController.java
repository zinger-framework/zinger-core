package com.food.ordering.ssn.controller;

import java.util.List;

import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.*;
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

import com.food.ordering.ssn.service.UserService;
import com.food.ordering.ssn.utils.Response;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping(value = "/customer")
	public Response<UserCollegeModel> insertCustomer(@RequestBody UserModel user) {
		return userService.insertCustomer(user);
	}

	@PostMapping(value = "/seller")
	public Response<UserShopListModel> insertSeller(@RequestBody UserModel user) {
		return userService.insertSeller(user);
	}

	@GetMapping(value = "")
	public Response<List<UserModel>> getAllUser(@RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
		return userService.getAllUser(oauthId);
	}

	@GetMapping(value = "/{oauth_id}")
	public Response<UserModel> getUserByOauthId(@PathVariable("oauth_id") String oauthId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = UserColumn.mobile) String mobile) {
		return userService.getUserByOauthId(oauthId, oauthIdRh);
	}

	@GetMapping(value = "/{mobile}")
	public Response<UserCollegeModel> getCollegeByMobile(@PathVariable("mobile") String mobile, @RequestHeader(value = "oauth_id") String oauthId, @RequestHeader(value = UserColumn.mobile) String mobileRh) {
		return userService.getCollegeByMobile(mobile, oauthId, mobileRh);
	}

	/**************************************************/

	@PatchMapping(value = "")
	public Response<String> updateUser(@RequestBody UserModel user, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
		return userService.updateUser(user, oauthId, mobile);
	}

	@PatchMapping(value = "/college")
	public Response<String> updateUserCollegeDetail(@RequestBody UserModel user, @RequestBody CollegeModel collegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
		return userService.updateUserCollege(user, collegeModel, oauthId, mobile);
	}

	/**************************************************/

	@DeleteMapping(value = "/{oauth_id}")
	public Response<UserModel> deleteUserByOauthId(@PathVariable("oauth_id") String oauthId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh) {
		return userService.deleteUserByOauthId(oauthId, oauthIdRh);
	}

	@PostMapping(value = "/customer")
	public Response<UserCollegeModel> insertUserCollege(@RequestBody UserModel user, @RequestBody CollegeModel collegeModel) {
		return userCollegeService.insertUserCollege(user);
	}


}
