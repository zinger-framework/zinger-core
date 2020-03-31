package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.CollegeModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.CollegeColumn.*;

public class CollegeRowMapperLambda {
    public static final RowMapper<CollegeModel> collegeRowMapperLambda = (rs, rownum) -> {
        CollegeModel college = new CollegeModel();
        college.setId(rs.getInt(id));
        college.setName(rs.getString(name));
        college.setIconUrl(rs.getString(iconUrl));
        college.setAddress(rs.getString(address));
        return college;
    };
}
