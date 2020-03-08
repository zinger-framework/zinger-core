package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.CollegeDao;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.utils.Response;
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
