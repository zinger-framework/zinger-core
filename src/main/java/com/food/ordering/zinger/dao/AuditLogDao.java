package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.logger.*;
import com.food.ordering.zinger.model.logger.*;
import com.food.ordering.zinger.query.AuditLogQuery;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Response<String> insertCollegeLog(CollegeLogModel collegeLogModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(CollegeLogColumn.id, collegeLogModel.getId())
                    .addValue(CollegeLogColumn.errorCode, collegeLogModel.getErrorCode())
                    .addValue(CollegeLogColumn.mobile, collegeLogModel.getMobile())
                    .addValue(CollegeLogColumn.message, collegeLogModel.getMessage())
                    .addValue(CollegeLogColumn.updatedValue, collegeLogModel.getUpdatedValue())
                    .addValue(CollegeLogColumn.priority, collegeLogModel.getPriority().name());

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

    public Response<String> insertShopLog(ShopLogModel ShopLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopLogColumn.id, ShopLogModel.getId())
                    .addValue(ShopLogColumn.errorCode, ShopLogModel.getErrorCode())
                    .addValue(ShopLogColumn.mobile, ShopLogModel.getMobile())
                    .addValue(ShopLogColumn.message, ShopLogModel.getMessage())
                    .addValue(ShopLogColumn.updatedValue, ShopLogModel.getUpdatedValue())
                    .addValue(ShopLogColumn.priority, ShopLogModel.getPriority().name());

            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertShopLog, parameters);
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

    public Response<String> insertUserLog(UserLogModel UserLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserLogColumn.usersMobile, UserLogModel.getUsersMobile())
                    .addValue(UserLogColumn.errorCode, UserLogModel.getErrorCode())
                    .addValue(UserLogColumn.mobile, UserLogModel.getMobile())
                    .addValue(UserLogColumn.message, UserLogModel.getMessage())
                    .addValue(UserLogColumn.updatedValue, UserLogModel.getUpdatedValue())
                    .addValue(UserLogColumn.priority, UserLogModel.getPriority().name());

            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertUserLog, parameters);
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

    public Response<String> insertItemLog(ItemLogModel ItemLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemLogColumn.id, ItemLogModel.getId())
                    .addValue(ItemLogColumn.errorCode, ItemLogModel.getErrorCode())
                    .addValue(ItemLogColumn.mobile, ItemLogModel.getMobile())
                    .addValue(ItemLogColumn.message, ItemLogModel.getMessage())
                    .addValue(ItemLogColumn.updatedValue, ItemLogModel.getUpdatedValue())
                    .addValue(ItemLogColumn.priority, ItemLogModel.getPriority().name());

            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertItemLog, parameters);
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


    public Response<String> insertOrderLog(OrderLogModel OrderLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(OrderLogColumn.id, OrderLogModel.getId())
                    .addValue(OrderLogColumn.errorCode, OrderLogModel.getErrorCode())
                    .addValue(OrderLogColumn.mobile, OrderLogModel.getMobile())
                    .addValue(OrderLogColumn.message, OrderLogModel.getMessage())
                    .addValue(OrderLogColumn.updatedValue, OrderLogModel.getUpdatedValue())
                    .addValue(OrderLogColumn.priority, OrderLogModel.getPriority().name());

            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertOrderLog, parameters);
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
