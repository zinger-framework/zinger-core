package com.food.ordering.ssn.controller;

import java.util.List;

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
    public Response<List<CollegeModel>> getAllColleges(@RequestHeader(value="oauth_id") String oauthId) {
        return collegeService.getAllColleges(oauthId);
    }
	
	@GetMapping(value = "/{college_id}")
    public Response<CollegeModel> getCollegeById(@PathVariable("college_id") Integer collegeId,@RequestHeader(value="oauth_id") String oauthIdRh) {
		return collegeService.getCollegeById(collegeId, oauthIdRh);
    }
	
	@PatchMapping
	public Response<CollegeModel> updateCollege(@RequestHeader(value = "oauth_id") String oauthId, @RequestHeader(value = "college_id") Integer collegeId, @RequestBody CollegeModel data){
		return collegeService.updateCollege(collegeId, oauthId, data);
	}
}
