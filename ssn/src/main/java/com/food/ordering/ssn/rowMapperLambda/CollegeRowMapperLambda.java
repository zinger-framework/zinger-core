package com.food.ordering.ssn.rowMapperLambda;

import static com.food.ordering.ssn.column.CollegeColumn.*;
import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.*;

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
