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

    /**************************************************/

    @PatchMapping(value = "/college")
    public Response<String> updateUserCollegeData(@RequestBody UserCollegeModel userCollegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return userService.updateUserCollegeData(userCollegeModel, oauthId, mobile);
    }
}
