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

    public Response<List<CollegeModel>> getAllColleges(String oauthId) {
        return collegeDao.getAllColleges(oauthId);
    }

    public Response<CollegeModel> getCollegeById(Integer collegeId, String oauthIdRh, String mobile) {
        return collegeDao.getCollegeById(collegeId, oauthIdRh, mobile);
    }

    public Response<String> updateCollege(CollegeModel collegeModel, String oauthId, String mobile) {
        return collegeDao.updateCollege(collegeModel, oauthId, mobile);
    }
}
