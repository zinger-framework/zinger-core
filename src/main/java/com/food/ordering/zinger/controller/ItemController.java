package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.service.interfaces.ItemService;
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
    public Response<String> insertItem(@RequestBody List<ItemModel> itemModelList) {
        return itemService.insertItem(itemModelList);
    }

    @GetMapping(value = getItemsByShopId)
    public Response<List<ItemModel>> getItemsByShopId(@PathVariable("shopId") Integer shopId) {
        return itemService.getItemsByShopId(shopId);
    }

    @GetMapping(value = getItemsByName)
    public Response<List<ItemModel>> getItemsByName(@PathVariable("placeId") Integer placeId, @PathVariable("itemName") String itemName) {
        return itemService.getItemsByName(placeId, itemName);
    }

    @PatchMapping(value = updateItem)
    public Response<String> updateItem(@RequestBody List<ItemModel> itemModelList) {
        return itemService.updateItem(itemModelList);
    }

    @DeleteMapping(value = deleteItemById)
    public Response<String> deleteItemById(@PathVariable("itemId") Integer itemId) {
        return itemService.deleteItemById(itemId);
    }
}
