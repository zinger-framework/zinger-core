package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.ApplicationLogColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.AuditLogDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.logger.ApplicationLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import static com.food.ordering.zinger.constant.Query.AuditLogQuery;

/**
 * AuditLogDao is responsible for CRUD operations in
 * Application_log table in MySQL.
 *
 * @implNote Please check corresponding actual table for better understanding.
 * @implNote All endpoint services are audited for both success and error responses
 * invoked here.
 */
@Repository
public class AuditLogDaoImpl implements AuditLogDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;



    /**
     * Inserts the HTTP request and response log
     *
     * @param applicationLogModel ApplicationLogModel
     * @return success response if the log insertion
     */
    @Override
    public Response<String> insertLog(ApplicationLogModel applicationLogModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ApplicationLogColumn.request_type, applicationLogModel.getRequestType().name())
                    .addValue(ApplicationLogColumn.endpoint_url, applicationLogModel.getEndpointUrl())
                    .addValue(ApplicationLogColumn.request_header, applicationLogModel.getRequestHeader())
                    .addValue(ApplicationLogColumn.request_object, applicationLogModel.getRequestObject())
                    .addValue(ApplicationLogColumn.response_object, applicationLogModel.getResponseObject());

            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertLog, parameters);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }


        return response;
    }

}
