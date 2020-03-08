package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.CollegeColumn;
import com.food.ordering.ssn.column.ItemColumn;
import com.food.ordering.ssn.column.ShopColumn;
import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.ItemModel;
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

    @GetMapping(value = "/{" + ShopColumn.id + "}")
    public Response<List<ItemModel>> getItemsByShopId(@PathVariable(ShopColumn.id) Integer shopId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByShopId(shopId, oauthId, mobile, role);
    }

    @GetMapping(value = "/{ " + CollegeColumn.id + "}/{" + ItemColumn.name + "}")
    public Response<List<ItemModel>> getItemsByName(@PathVariable(CollegeColumn.id) Integer collegeId, @PathVariable(ItemColumn.name) String itemName, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return itemService.getItemsByName(collegeId, itemName, oauthId, mobile, role);
    }
}
