package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.CollegeModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.CollegeColumn.*;

public class CollegeRowMapperLambda {
    public static final RowMapper<CollegeModel> collegeRowMapperLambda = (rs, rownum) -> {
        CollegeModel college = new CollegeModel();
        college.setId(rs.getInt(id));
        college.setName(rs.getString(name));
        college.setIconUrl(rs.getString(iconUrl));
        college.setAddress(rs.getString(address));
        college.setIsDelete(rs.getInt(isDelete));
        return college;
    };
}
