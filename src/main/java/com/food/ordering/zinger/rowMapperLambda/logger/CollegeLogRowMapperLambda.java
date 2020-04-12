package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.model.logger.CollegeLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.CollegeLogColumn.*;

public class CollegeLogRowMapperLambda {
    public static final RowMapper<CollegeLogModel> collegeLogRowMapperLambda = (rs, rownum) -> {
        CollegeLogModel college = new CollegeLogModel();
        college.setId(rs.getInt(id));
        college.setErrorCode(rs.getInt(errorCode));
        college.setMobile(rs.getString(mobile));
        college.setMessage(rs.getString(message));
        college.setUpdatedValue(rs.getString(updatedValue));
        college.setDate(rs.getTimestamp(date));
        college.setPriority(Priority.valueOf(rs.getString(priority)));
        return college;
    };
}
