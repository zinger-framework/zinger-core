package com.food.ordering.zinger.service;

import com.food.ordering.zinger.dao.PlaceDao;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    @Autowired
    PlaceDao placeDao;

    public Response<String> insertCollege(PlaceModel placeModel, String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return placeDao.insertCollege(placeModel, requestHeaderModel);
    }

    public Response<List<PlaceModel>> getAllColleges(String oauthId, String mobile, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, mobile, role);
        return placeDao.getAllColleges(requestHeaderModel);
    }
}
