package com.food.ordering.ssn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@PostMapping(value = "/insertCollege")
	public Response<CollegeModel> insertCollege(@RequestBody CollegeModel college, @RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken){
		return collegeService.insertCollege(college, oauthId, accessToken);
	}
	
	@GetMapping(value = "")
    public Response<List<CollegeModel>> getAllUser(@RequestHeader(value="oauth_id") String oauthId, @RequestHeader(value="access_token") String accessToken) {
        return collegeService.getAllColleges(oauthId,accessToken);
    }
	
	@GetMapping(value = "/{college_id}")
    public Response<CollegeModel> getCollegeById(@PathVariable("college_id") Integer collegeId,@RequestHeader(value="oauth_id") String oauthIdRh, @RequestHeader(value="access_token") String accessToken) {
		return collegeService.getCollegeById(collegeId, oauthIdRh, accessToken);
    }
}
