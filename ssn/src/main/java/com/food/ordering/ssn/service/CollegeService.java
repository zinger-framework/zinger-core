package com.food.ordering.ssn.service;

import com.food.ordering.ssn.dao.CollegeDao;
import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeService {

    @Autowired
    CollegeDao collegeDao;

    public Response<List<CollegeModel>> getAllColleges(String oauthId, String mobile, String role) {
        return collegeDao.getAllColleges(oauthId, mobile, role);
    }
}
