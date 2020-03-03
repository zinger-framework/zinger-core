package com.food.ordering.ssn.controller;

import java.util.List;

import com.food.ordering.ssn.column.UserColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.service.CollegeService;
import com.food.ordering.ssn.utils.Response;

@RestController
@RequestMapping("/college")
public class CollegeController {

    @Autowired
    CollegeService collegeService;

    @GetMapping(value = "")
    public Response<List<CollegeModel>> getAllColleges(@RequestHeader(value = "oauth_id") String oauthId) {
        return collegeService.getAllColleges(oauthId);
    }

    @GetMapping(value = "/{college_id}")
    public Response<CollegeModel> getCollegeById(@PathVariable("college_id") Integer collegeId, @RequestHeader(value = "oauth_id") String oauthIdRh, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return collegeService.getCollegeById(collegeId, oauthIdRh, mobile);
    }

    @PatchMapping
    public Response<String> updateCollege(@RequestBody CollegeModel collegeModel, @RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return collegeService.updateCollege(collegeModel, oauthId, mobile);
    }
}
