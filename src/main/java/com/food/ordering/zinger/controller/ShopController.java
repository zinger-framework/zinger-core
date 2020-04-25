package com.food.ordering.zinger.controller;

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
    public Response<String> insertShop(@RequestBody ConfigurationModel configurationModel) {
        return shopService.insertShop(configurationModel);
    }

    @GetMapping(value = getShopsByPlaceId)
    public Response<List<ShopConfigurationModel>> getShopsByPlaceId(@PathVariable("placeId") Integer placeId) {
        return shopService.getShopByPlaceId(placeId);
    }

    @PatchMapping(value = updateShopConfiguration)
    Response<String> updateShopConfiguration(@RequestBody ConfigurationModel configurationModel) {
        return shopService.updateShopConfiguration(configurationModel);
    }
}
