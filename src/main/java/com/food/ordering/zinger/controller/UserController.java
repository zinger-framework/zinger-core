package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.notification.UserNotificationModel;
import com.food.ordering.zinger.service.interfaces.UserService;
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
    public Response<UserPlaceModel> loginRegisterCustomer(@RequestBody UserModel user) {
        return userService.loginRegisterCustomer(user);
    }

    @PostMapping(value = verifySeller)
    public Response<UserShopListModel> verifySeller(@RequestBody UserModel user) {
        return userService.verifySeller(user);
    }

    /**************************************************/

    @GetMapping(value = getSellerByShopId)
    public Response<List<UserModel>> getSellerByShopId(@PathVariable("shopId") Integer shopId) {
        return userService.getSellerByShopId(shopId);
    }

    /**************************************************/

    @PatchMapping(value = updateUser)
    public Response<String> updateUser(@RequestBody UserModel userModel) {
        return userService.updateUser(userModel);
    }

    @PatchMapping(value = updateUserNotificationToken)
    public Response<String> updateUserNotificationToken(@RequestBody UserNotificationModel userNotificationModel) {
        return userService.updateUserNotificationToken(userNotificationModel);
    }

    @PatchMapping(value = updateUserPlaceData)
    public Response<String> updateUserPlaceData(@RequestBody UserPlaceModel userPlaceModel) {
        return userService.updateUserPlaceData(userPlaceModel);
    }
    /**************************************************/

    @DeleteMapping(value = deleteSeller)
    public Response<String> deleteSeller(@PathVariable("shopId") Integer shopId, @PathVariable("userId") Integer userId) {
        return userService.deleteSeller(shopId, userId);
    }
}
