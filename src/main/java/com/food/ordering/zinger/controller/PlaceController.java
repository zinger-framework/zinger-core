package com.food.ordering.zinger.controller;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.service.interfaces.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.food.ordering.zinger.constant.ApiConfig.PlaceApi.*;

@RestController
@RequestMapping(BASE_URL)
public class PlaceController {

    @Autowired
    PlaceService placeService;

    @PostMapping(value = insertPlace)
    public Response<String> insertPlace(@RequestBody PlaceModel placeModel) {
        return placeService.insertPlace(placeModel);
    }

    @GetMapping(value = getAllPlaces)
    public Response<List<PlaceModel>> getAllPlaces() {
        return placeService.getAllPlaces();
    }
}
