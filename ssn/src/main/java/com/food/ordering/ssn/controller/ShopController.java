package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.CollegeColumn;
import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.service.ShopService;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

    @Autowired
    ShopService shopService;

    @GetMapping(value = "/{" + CollegeColumn.id + "}")
    public Response<List<ShopModel>> getShopsByCollegeId(@PathVariable(CollegeColumn.id) Integer collegeId, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return shopService.getShopByCollegeId(collegeId, oauthId, mobile, role);
    }
}
