package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.service.PlaceService;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.food.ordering.zinger.constant.ApiConfig.CollegeApi.*;

@RestController
@RequestMapping(BASE_URL)
public class PlaceController {

    @Autowired
    PlaceService placeService;

    @PostMapping(value = insertCollege)
    public Response<String> insertCollege(@RequestBody PlaceModel placeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return placeService.insertCollege(placeModel, oauthId, mobile, role);
    }

    @GetMapping(value = getAllColleges)
    public Response<List<PlaceModel>> getAllColleges(@RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return placeService.getAllColleges(oauthId, mobile, role);
    }
}
