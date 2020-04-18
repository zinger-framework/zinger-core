package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.PlaceColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.PlaceQuery;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.logger.PlaceLogModel;
import com.food.ordering.zinger.rowMapperLambda.PlaceRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;

public interface PlaceDao {
    Response<String> insertPlace(PlaceModel placeModel, RequestHeaderModel requestHeaderModel);

    Response<List<PlaceModel>> getAllPlaces(RequestHeaderModel requestHeaderModel);

    Response<PlaceModel> getPlaceById(Integer placeId);
}
