package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserPlaceModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.model.notification.UserNotificationModel;
import com.food.ordering.zinger.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
