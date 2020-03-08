package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.UserCollegeModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.model.UserShopListModel;
import com.food.ordering.ssn.service.UserService;
import com.food.ordering.ssn.utils.Response;
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

    @PatchMapping(value = "/college")
    public Response<String> updateUserCollegeData(@RequestBody UserCollegeModel userCollegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return userService.updateUserCollegeData(userCollegeModel, oauthId, mobile, role);
    }
}
