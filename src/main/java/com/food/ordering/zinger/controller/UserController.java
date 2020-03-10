package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.model.UserCollegeModel;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.service.UserService;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping(value = "")
    public Response<String> updateUser(@RequestBody UserModel userModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return userService.updateUser(userModel, oauthId, mobile, role);
    }

    @PatchMapping(value = "/college")
    public Response<String> updateUserCollegeData(@RequestBody UserCollegeModel userCollegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return userService.updateUserCollegeData(userCollegeModel, oauthId, mobile, role);
    }
}
