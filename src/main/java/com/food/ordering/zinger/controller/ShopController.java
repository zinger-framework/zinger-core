package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.service.ShopService;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.food.ordering.zinger.constant.ApiConfig.ShopApi.*;

@RestController
@RequestMapping(value = BASE_URL)
public class ShopController {

    @Autowired
    ShopService shopService;

    @PostMapping(value = insertShop)
    public Response<String> insertShop(@RequestBody ConfigurationModel configurationModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.insertShop(configurationModel, oauthId, mobile, role);
    }

    @GetMapping(value = getShopsByCollegeId)
    public Response<List<ShopConfigurationModel>> getShopsByCollegeId(@PathVariable("placeId") Integer placeId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.getShopByCollegeId(placeId, oauthId, mobile, role);
    }

    @PatchMapping(value = updateShopConfiguration)
    Response<String> updateShopConfiguration(@RequestBody ConfigurationModel configurationModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.updateShopConfiguration(configurationModel, oauthId, mobile, role);
    }
}
