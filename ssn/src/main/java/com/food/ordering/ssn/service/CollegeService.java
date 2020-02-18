package com.food.ordering.ssn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.food.ordering.ssn.dao.CollegeDao;
import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.utils.Response;

@Service
public class CollegeService {

	@Autowired
	CollegeDao collegeDao;
	
	public Response<CollegeModel> insertCollege(CollegeModel college,String oauthId, String accessToken){
		return collegeDao.insertCollege(college,oauthId,accessToken);
	}
	
	public Response<List<CollegeModel>> getAllColleges(String oauthId, String accessToken) {
		return collegeDao.getAllUser(oauthId,accessToken);
	}
	
	public Response<CollegeModel> getCollegeById(Integer collegeID,String oauthIdRh, String accessToken) {
		return collegeDao.getCollegeById(collegeID,oauthIdRh,accessToken);
    }
}
