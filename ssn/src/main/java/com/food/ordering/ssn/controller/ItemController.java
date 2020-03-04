package com.food.ordering.ssn.controller;

import java.util.List;

import com.food.ordering.ssn.column.UserColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.service.ItemService;
import com.food.ordering.ssn.utils.Response;

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

    @GetMapping(value = "/searchMenu/{query}")
    public Response<List<ItemModel>> getItemsByQuery(@RequestHeader(value = UserColumn.oauthId) String oauthId, @PathVariable("query") String query) {
        return itemService.getItemsByQuery(oauthId, query);
    }
}
