package com.food.ordering.zinger.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.zinger.column.CollegeLogColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.CollegeLogModel;
import com.food.ordering.zinger.query.AuditLogQuery;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;

@Repository
public class AuditLogDao {
	 @Autowired
	    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	    @Autowired
	    UtilsDao utilsDao;

	    public Response<String> insertCollegeLog(CollegeLogModel collegeLogModel) {
	        Response<String> response = new Response<>();

	        try {
	            
	            SqlParameterSource parameters = new MapSqlParameterSource()
	                    .addValue(CollegeLogColumn.id, collegeLogModel.getId())
	                    .addValue(CollegeLogColumn.errorCode, collegeLogModel.getErrorCode())
	                    .addValue(CollegeLogColumn.mobile, collegeLogModel.getMobile())
	            		.addValue(CollegeLogColumn.message, collegeLogModel.getMessage())
	            		.addValue(CollegeLogColumn.updatedValue, collegeLogModel.getUpdatedValue())
	            		.addValue(CollegeLogColumn.date, collegeLogModel.getDate())
	            		.addValue(CollegeLogColumn.priority, collegeLogModel.getPriority());
	            		
	            		
	            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertCollegeLog, parameters);
	            if (responseValue > 0) {
	                response.setCode(ErrorLog.CodeSuccess);
	                response.setMessage(ErrorLog.Success);
	                response.setData(ErrorLog.Success);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return response;
	    }

}
