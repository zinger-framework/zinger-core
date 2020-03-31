package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.service.ItemService;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/menu")
public class ItemController {

    @Autowired
    ItemService itemService;


    @PostMapping(value = "")
    public Response<String> insertItem(@RequestBody ItemModel itemModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.insertItem(itemModel, oauthId, mobile, role);
    }

    @GetMapping(value = "/shop/{shopId}")
    public Response<List<ItemModel>> getItemsByShopId(@PathVariable("shopId") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByShopId(shopId, oauthId, mobile, role);
    }

    @GetMapping(value = "/{collegeId}/{itemName}")
    public Response<List<ItemModel>> getItemsByName(@PathVariable("collegeId") Integer collegeId, @PathVariable("itemName") String itemName, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByName(collegeId, itemName, oauthId, mobile, role);
    }

    @PatchMapping(value = "")
    public Response<String> updateItemById(@RequestBody ItemModel itemModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.updateItemById(itemModel, oauthId, mobile, role);
    }

    @DeleteMapping(value = "/delete/{itemId}")
    public Response<String> deleteItemById(@PathVariable("itemId") Integer itemId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.deleteItemById(itemId, oauthId, mobile, role);
    }

    @DeleteMapping(value = "/undelete/{itemId}")
    public Response<String> unDeleteItemById(@PathVariable("itemId") Integer itemId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.unDeleteItemById(itemId, oauthId, mobile, role);
    }

}
