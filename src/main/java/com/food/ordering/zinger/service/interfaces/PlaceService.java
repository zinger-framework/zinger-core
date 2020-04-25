package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.Response;

import java.util.List;

public interface PlaceService {

    Response<String> insertPlace(PlaceModel placeModel);

    Response<List<PlaceModel>> getAllPlaces();
}
