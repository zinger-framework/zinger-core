package com.food.ordering.ssn.controller;

import java.util.List;

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
	
	@GetMapping(value = "/getOutlets/{college_id}")
    public Response<List<ShopModel>> getShopsByCollegeId(@PathVariable("college_id") Integer collegeId,@RequestHeader(value="oauth_id") String oauthIdRh) {
		return shopService.getShopByCollegeId(collegeId, oauthIdRh);
    }

}
