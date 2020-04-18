package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.UserQuery;
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

public interface InterceptorDao {
    Response<UserModel> validateUser(RequestHeaderModel requestHeaderModel);
}
