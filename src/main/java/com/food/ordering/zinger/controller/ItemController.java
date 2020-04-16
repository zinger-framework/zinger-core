package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.food.ordering.zinger.constant.ApiConfig.ItemApi.*;

@RestController
@RequestMapping(value = BASE_URL)
public class ItemController {

    @Autowired
    ItemService itemService;


    @PostMapping(value = insertItem)
    public Response<String> insertItem(@RequestBody List<ItemModel> itemModelList, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.insertItem(itemModelList, oauthId, mobile, role);
    }

    @GetMapping(value = getItemsByShopId)
    public Response<List<ItemModel>> getItemsByShopId(@PathVariable("shopId") Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByShopId(shopId, oauthId, mobile, role);
    }

    @GetMapping(value = getItemsByName)
    public Response<List<ItemModel>> getItemsByName(@PathVariable("placeId") Integer placeId, @PathVariable("itemName") String itemName, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByName(placeId, itemName, oauthId, mobile, role);
    }

    @PatchMapping(value = updateItemById)
    public Response<String> updateItemById(@RequestBody ItemModel itemModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.updateItemById(itemModel, oauthId, mobile, role);
    }

    @DeleteMapping(value = deleteItemById)
    public Response<String> deleteItemById(@PathVariable("itemId") Integer itemId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.deleteItemById(itemId, oauthId, mobile, role);
    }

    @DeleteMapping(value = unDeleteItemById)
    public Response<String> unDeleteItemById(@PathVariable("itemId") Integer itemId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.unDeleteItemById(itemId, oauthId, mobile, role);
    }
}
