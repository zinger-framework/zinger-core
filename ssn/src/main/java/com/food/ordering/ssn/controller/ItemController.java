package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.service.ItemService;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/menu")
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping(value = "/shop")
    public Response<List<ItemModel>> getItemsByShopId(@RequestBody ShopModel shopModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByShopId(shopModel, oauthId, mobile, role);
    }

    @GetMapping(value = "/{collegeId}/{itemName}")
    public Response<List<ItemModel>> getItemsByName(@PathVariable("collegeId") Integer collegeId, @PathVariable("itemName") String itemName, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByName(collegeId, itemName, oauthId, mobile, role);
    }
}
