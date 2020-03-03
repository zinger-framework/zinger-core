package com.food.ordering.ssn.controller;

import java.util.List;

import com.food.ordering.ssn.model.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.service.ShopService;
import com.food.ordering.ssn.utils.Response;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

	@Autowired
	ShopService shopService;
	
	@GetMapping(value = "/all/{college_id}")
    public Response<List<ShopModel>> getShopsByCollegeId(@PathVariable("college_id") Integer collegeId,@RequestHeader(value="oauth_id") String oauthIdRh,@RequestHeader(value="mobile") String mobile) {
		return shopService.getShopByCollegeId(collegeId, oauthIdRh,mobile);
	}

	@GetMapping(value="/all/{shop_id}")
	public Response<List<ItemModel>> getMenuByShopId(@PathVariable("shop_id") Integer shopId,@RequestHeader(value="oauth_id") String oauthIdRh,@RequestHeader(value="mobile") String mobile){
		return shopService.getMenuByShopId(shopId,oauthIdRh,mobile);
	}

	@GetMapping(value="/all/{college_id}/{item_name}")
	public Response<List<ItemModel>> getItemsByName(@PathVariable("college_id") Integer collegeId,@PathVariable("item_name")String itemName,@RequestHeader(value="oauth_id") String oauthIdRh,@RequestHeader(value="mobile") String mobile){
		return shopService.getItemsByName(collegeId,itemName,oauthIdRh,mobile);
	}


}
