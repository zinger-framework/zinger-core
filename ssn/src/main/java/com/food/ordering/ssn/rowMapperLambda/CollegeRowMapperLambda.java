package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.CollegeModel;

public class CollegeRowMapperLambda {
	public static final RowMapper<CollegeModel> collegeRowMapperLambda = (rs, rownum) -> {
		CollegeModel college = new CollegeModel();
		college.setID(rs.getInt("id"));
		college.setName(rs.getString("name"));
		college.setIconUrl(rs.getString("icon_url"));
		college.setAddress(rs.getString("address"));
		college.setIsDelete(rs.getInt("is_delete"));
		return college;
	};
}
