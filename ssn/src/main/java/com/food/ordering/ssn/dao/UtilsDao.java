package com.food.ordering.ssn.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.*;
import com.food.ordering.ssn.query.*;
import com.food.ordering.ssn.rowMapperLambda.*;
import com.food.ordering.ssn.utils.*;
import com.food.ordering.ssn.column.*;

@Repository
public class UtilsDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Response<UserModel> validateUser(String oauthId, String mobile) {
        UserModel userModel = null;
        Response<UserModel> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.oauthId, oauthId)
                    .addValue(UserColumn.mobile, mobile);

            userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.validateUser, parameters, UserRowMapperLambda.userRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (userModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userModel);
            }
        }
        return response;
    }
}
