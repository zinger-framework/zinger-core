package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.CollegeLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.CollegeLogColumn.*;

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