package com.food.ordering.ssn.controller;

import com.food.ordering.ssn.column.CollegeColumn;
import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.service.CollegeService;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {

    @Autowired
    CollegeService collegeService;

    @GetMapping(value = "")
    public Response<List<CollegeModel>> getAllColleges(@RequestHeader(value = UserColumn.oauthId) String oauthId, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return collegeService.getAllColleges(oauthId, mobile);
    }

    @GetMapping(value = "/{" + CollegeColumn.id + "}")
    public Response<CollegeModel> getCollegeById(@PathVariable(CollegeColumn.id) Integer collegeId, @RequestHeader(value = UserColumn.oauthId) String oauthIdRh, @RequestHeader(value = UserColumn.mobile) String mobile) {
        return collegeService.getCollegeById(collegeId, oauthIdRh, mobile);
    }
}
