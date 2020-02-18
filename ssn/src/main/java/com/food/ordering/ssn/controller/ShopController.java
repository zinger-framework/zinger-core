package com.food.ordering.ssn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@PostMapping(value = "/insertShop")
    public Response<ShopModel> insertShop(@RequestBody ShopModel shop, @RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
		return shopService.createShop(shop, oauthId, accessToken);
    }
	
	@GetMapping(value = "")
    public Response<List<ShopModel>> getAllShops(@RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
        return shopService.getAllShops(oauthId,accessToken);
    }
	
	@GetMapping(value = "/college/{college_id}")
    public Response<List<ShopModel>> getShopsByCollegeId(@PathVariable("college_id") Integer collegeId,@RequestHeader(value="oauth_id") String oauthIdRh, @RequestHeader(value="access_token") String accessToken) {
		return shopService.getShopByCollegeId(collegeId, oauthIdRh, accessToken);
    }
	
	@GetMapping(value = "/{shop_id}")
    public Response<ShopModel> getShopById(@PathVariable("shop_id") Integer shopId,@RequestHeader(value="oauth_id") String oauthIdRh, @RequestHeader(value="access_token") String accessToken) {
		return shopService.getShopById(shopId, oauthIdRh, accessToken);
    }
	
	@PatchMapping(value = "")
    public Response<ShopModel> updateShopById(@RequestBody ShopModel shop,@RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
		return shopService.updateShopById(shop,oauthId,accessToken);
	}
	
	@DeleteMapping(value = "/{shop_id}")
    public Response<ShopModel> deleteUserByOauthId(@PathVariable("shop_id") Integer shopId,@RequestHeader(value="oauth_id") String oauthIdRh, @RequestHeader(value="access_token") String accessToken) {
		return shopService.deleteShopById(shopId,oauthIdRh,accessToken);
    }
	
}
