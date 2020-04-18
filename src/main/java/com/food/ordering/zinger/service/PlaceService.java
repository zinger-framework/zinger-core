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

    public Response<String> insertPlace(PlaceModel placeModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return placeDao.insertPlace(placeModel, requestHeaderModel);
    }

    public Response<List<PlaceModel>> getAllPlaces(String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return placeDao.getAllPlaces(requestHeaderModel);
    }
}
