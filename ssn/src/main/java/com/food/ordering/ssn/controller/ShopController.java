package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.service.ShopService;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

    @Autowired
    ShopService shopService;

    @GetMapping(value = "/all/{college_id}")
    public Response<List<ShopModel>> getShopsByCollegeId(@PathVariable("college_id") Integer collegeId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = "mobile") String mobile) {
        return shopService.getShopByCollegeId(collegeId, oauthIdRh, mobile);
    }

    @GetMapping(value = "/{shop_id}")
    public Response<ShopModel> getShopById(@PathVariable("shop_id") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = "mobile") String mobile) {
        return shopService.getShopById(shopId, oauthIdRh, mobile);
    }

    @GetMapping(value = "/all/{shop_id}")
    public Response<List<ItemModel>> getMenuByShopId(@PathVariable("shop_id") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = "mobile") String mobile) {
        return shopService.getMenuByShopId(shopId, oauthIdRh, mobile);
    }

    @GetMapping(value = "/all/{college_id}/{item_name}")
    public Response<List<ItemModel>> getItemsByName(@PathVariable("college_id") Integer collegeId, @PathVariable("item_name") String itemName, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = "mobile") String mobile) {
        return shopService.getItemsByName(collegeId, itemName, oauthIdRh, mobile);
    }


}
