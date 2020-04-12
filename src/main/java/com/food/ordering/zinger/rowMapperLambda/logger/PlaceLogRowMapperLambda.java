package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.model.logger.PlaceLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.PlaceLogColumn.*;

public class PlaceLogRowMapperLambda {
    public static final RowMapper<PlaceLogModel> collegeLogRowMapperLambda = (rs, rownum) -> {
        PlaceLogModel place = new PlaceLogModel();
        place.setId(rs.getInt(id));
        place.setErrorCode(rs.getInt(errorCode));
        place.setMobile(rs.getString(mobile));
        place.setMessage(rs.getString(message));
        place.setUpdatedValue(rs.getString(updatedValue));
        place.setDate(rs.getTimestamp(date));
        place.setPriority(Priority.valueOf(rs.getString(priority)));
        return place;
    };
}
