package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.UserCollegeModel;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.service.UserService;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.food.ordering.zinger.constant.ApiConfig.UserApi.*;

@RestController
@RequestMapping(BASE_URL)
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = loginRegisterCustomer)
    public Response<UserCollegeModel> loginRegisterCustomer(@RequestBody UserModel user) {
        return userService.loginRegisterCustomer(user);
    }

    @PostMapping(value = verifySeller)
    public Response<UserShopListModel> verifySeller(@RequestBody UserModel user) {
        return userService.verifySeller(user);
    }

    @PostMapping(value = insertSeller)
    public Response<String> insertSeller(@PathVariable("shopId") Integer shopId, @PathVariable("mobile") String mobile, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobileRh, @RequestHeader(value = UserColumn.role) String role) {
        return userService.insertSeller(shopId, mobile, oauthId, mobileRh, role);
    }

    /**************************************************/

    @GetMapping(value = getSellerByShopId)
    public Response<List<UserModel>> getSellerByShopId(@PathVariable("shopId") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobileRh, @RequestHeader(value = UserColumn.role) String role) {
        return userService.getSellerByShopId(shopId, oauthId, mobileRh, role);
    }

    /**************************************************/

    @PatchMapping(value = updateUser)
    public Response<String> updateUser(@RequestBody UserModel userModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return userService.updateUser(userModel, oauthId, mobile, role);
    }

    @PatchMapping(value = updateUserCollegeData)
    public Response<String> updateUserCollegeData(@RequestBody UserCollegeModel userCollegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return userService.updateUserCollegeData(userCollegeModel, oauthId, mobile, role);
    }

    /**************************************************/

    @DeleteMapping(value = deleteSeller)
    public Response<String> deleteSeller(@PathVariable("shopId") Integer shopId, @PathVariable("mobile") String mobile, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobileRh, @RequestHeader(value = UserColumn.role) String role) {
        return userService.deleteSeller(shopId, mobile, oauthId, mobileRh, role);
    }
}
