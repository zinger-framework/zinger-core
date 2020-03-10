package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.service.CollegeService;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {

    @Autowired
    CollegeService collegeService;

    @PostMapping(value = "")
    public Response<String> insertCollege(@RequestBody CollegeModel collegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return collegeService.insertCollege(collegeModel, oauthId, mobile, role);
    }

    @GetMapping(value = "")
    public Response<List<CollegeModel>> getAllColleges(@RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile, @RequestHeader(value = UserColumn.role) String role) {
        return collegeService.getAllColleges(oauthId, mobile, role);
    }
}
