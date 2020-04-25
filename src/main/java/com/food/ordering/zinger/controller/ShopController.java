package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.service.interfaces.ShopService;
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
    public Response<String> insertShop(@RequestBody ConfigurationModel configurationModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.id) Integer id, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.insertShop(configurationModel, oauthId, id, role);
    }

    @GetMapping(value = getShopsByPlaceId)
    public Response<List<ShopConfigurationModel>> getShopsByPlaceId(@PathVariable("placeId") Integer placeId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.id) Integer id, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.getShopByPlaceId(placeId, oauthId, id, role);
    }

    @PatchMapping(value = updateShopConfiguration)
    Response<String> updateShopConfiguration(@RequestBody ConfigurationModel configurationModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.id) Integer id, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.updateShopConfiguration(configurationModel, oauthId, id, role);
    }
}
