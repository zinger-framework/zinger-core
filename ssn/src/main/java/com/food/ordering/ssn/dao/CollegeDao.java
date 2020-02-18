package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.query.CollegeQuery;
import com.food.ordering.ssn.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class CollegeDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	

	public Response<CollegeModel> insertCollege(CollegeModel college,String oauthId, String accessToken){
		Response<CollegeModel> response = new Response<>();
		
		try {
			
			if(new UtilsDao().validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
				return response;
			
			
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("name", college.getName())
					.addValue("icon_url", college.getIconUrl())
					.addValue("address",college.getAddress());
			
			jdbcTemplate.update(CollegeQuery.insertCollege, parameters);
			college.setIsDelete(0);
			
			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
			response.setData(college);
		}catch(Exception e) {
			e.printStackTrace();
		} 
		
		return response;
	}
	
	public Response<CollegeModel> getCollegeById(Integer collegeId,String oauthIdRh, String accessToken) {
		CollegeModel college = null;
		Response<CollegeModel> response = new Response<>();
		
		if(new UtilsDao().validateUser(oauthIdRh, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("college_id", collegeId);
			college = jdbcTemplate.queryForObject(CollegeQuery.getCollegeById, parameters, CollegeRowMapperLambda.collegeRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(college != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(college);
			}
		}
		return response;
	}
	
	public Response<List<CollegeModel>> getAllColleges(String oauthId, String accessToken) {
		Response<List<CollegeModel>> response = new Response<>();
		List<CollegeModel> list = null;
		
		if(new UtilsDao().validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			list = jdbcTemplate.query(CollegeQuery.getAllColleges, CollegeRowMapperLambda.collegeRowMapperLambda);
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(list != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(list);
			}
		}
		return response;
	}
	
	
	
}
