package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.UserQuery;
import com.food.ordering.zinger.dao.interfaces.InterceptorDao;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.rowMapperLambda.UserRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class InterceptorDaoImpl implements InterceptorDao {

    @Autowired
    Environment env;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Response<UserModel> validateUser(RequestHeaderModel requestHeaderModel) {
        UserModel userModel = null;
        Response<UserModel> response = new Response<>();

        try {
            if (requestHeaderModel.getOauthId().equals(env.getProperty(Constant.authIdSA)) && requestHeaderModel.getId().equals(env.getProperty(Constant.idSA)) && requestHeaderModel.getRole().equals(env.getProperty(Constant.roleSA))) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.oauthId, requestHeaderModel.getOauthId())
                    .addValue(UserColumn.id, requestHeaderModel.getId())
                    .addValue(UserColumn.role, requestHeaderModel.getRole());

            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.validateUser, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
