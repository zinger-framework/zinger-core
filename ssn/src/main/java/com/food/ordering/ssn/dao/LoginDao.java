package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.query.LoginQuery;
import com.food.ordering.ssn.rowMapperLambda.LoginRowMapperLambda;

@Repository
public class LoginDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<UserModel> getAllUser() {
		return jdbcTemplate.query(LoginQuery.getAllUser, LoginRowMapperLambda.userRowMapperLambda);
	}
    
    public UserModel getSellerById(Integer id) {
		SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
		
		return namedParameterJdbcTemplate.queryForObject(LoginQuery.getUserById, parameters, LoginRowMapperLambda.userRowMapperLambda);
	}
    
    public Integer deleteUser(Integer id) {
    	return 0;
    	//SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
		//return namedParameterJdbcTemplate.queryForObject(LoginQuery.deleteUserById, parameters, LoginRowMapperLambda.userRowMapperLambda);
    } 
}
