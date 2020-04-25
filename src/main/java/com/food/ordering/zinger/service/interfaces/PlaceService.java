package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.dao.impl.PlaceDaoImpl;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlaceService {

    Response<String> insertPlace(PlaceModel placeModel);

    Response<List<PlaceModel>> getAllPlaces();
}
