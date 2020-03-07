package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.service.ItemService;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping(value = "/getMenuItems/{shop_id}")
    public Response<List<ItemModel>> getItemsByShopId(@PathVariable("shop_id") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return itemService.getItemsByShopId(shopId, oauthIdRh, mobile);
    }

    @GetMapping(value = "/{item_id}")
    public Response<ItemModel> getItemById(@PathVariable("item_id") Integer itemId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return itemService.getItemById(itemId, oauthIdRh, mobile);
    }

    @GetMapping(value = "/menu/{college_id}/{item_name}")
    public Response<List<ItemModel>> getItemsByName(@PathVariable("college_id") Integer collegeId, @PathVariable("item_name") String itemName, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return itemService.getItemsByName(collegeId, itemName, oauthId, mobile);
    }
}
