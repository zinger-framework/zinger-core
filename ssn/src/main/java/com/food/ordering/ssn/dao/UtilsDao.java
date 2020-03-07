package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.query.UserQuery;
import com.food.ordering.ssn.rowMapperLambda.UserRowMapperLambda;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

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
