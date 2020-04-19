package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.*;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.AuditLogDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.logger.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import static com.food.ordering.zinger.constant.Query.AuditLogQuery;

/**
 * AuditLogDao is responsible for CRUD operations in
 * ShopLog, PlaceLog, UserLog, OrderLog and ItemLog tables in MySQL.
 *
 * @implNote Please check corresponding actual table for better understanding.
 *
 * @implNote All endpoint services are audited for both success and error responses
 * invoked here.
 */
@Repository
public class AuditLogDaoImpl implements AuditLogDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Inserts the place log details.
     *
     * @param placeLogModel PlaceLogModel
     * @return success response if the insertion is successful.
     */
    @Override
    public Response<String> insertPlaceLog(PlaceLogModel placeLogModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(PlaceLogColumn.id, placeLogModel.getId())
                    .addValue(PlaceLogColumn.errorCode, placeLogModel.getErrorCode())
                    .addValue(PlaceLogColumn.userId, placeLogModel.getUserId())
                    .addValue(PlaceLogColumn.message, placeLogModel.getMessage())
                    .addValue(PlaceLogColumn.updatedValue, placeLogModel.getUpdatedValue())
                    .addValue(PlaceLogColumn.priority, placeLogModel.getPriority().name());

            int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertPlaceLog, parameters);
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

    /**
     * Inserts the shop log details.
     *
     * @param ShopLogModel ShopLogModel
     * @return success response if the insertion is successful.
     */
    @Override
    public Response<String> insertShopLog(ShopLogModel ShopLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopLogColumn.id, ShopLogModel.getId())
                    .addValue(ShopLogColumn.errorCode, ShopLogModel.getErrorCode())
                    .addValue(ShopLogColumn.userId, ShopLogModel.getUserId())
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Inserts the user log details.
     *
     * @param UserLogModel UserLogModel
     * @return success response if the insertion is successful.
     */
    @Override
    public Response<String> insertUserLog(UserLogModel UserLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserLogColumn.id, UserLogModel.getId())
                    .addValue(UserLogColumn.errorCode, UserLogModel.getErrorCode())
                    .addValue(UserLogColumn.userId, UserLogModel.getUserId())
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Inserts the item log details.
     *
     * @param ItemLogModel ItemLogModel
     * @return success response if the insertion is successful.
     */
    @Override
    public Response<String> insertItemLog(ItemLogModel ItemLogModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemLogColumn.id, ItemLogModel.getId())
                    .addValue(ItemLogColumn.errorCode, ItemLogModel.getErrorCode())
                    .addValue(ItemLogColumn.userId, ItemLogModel.getUserId())
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Inserts the order log details.
     *
     * @param OrderLogModel OrderLogModel
     * @return success response if the insertion is successful.
     */
    @Override
    public Response<String> insertOrderLog(OrderLogModel OrderLogModel) {
        Response<String> response = new Response<>();

        try {

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(OrderLogColumn.id, OrderLogModel.getId())
                    .addValue(OrderLogColumn.errorCode, OrderLogModel.getErrorCode())
                    .addValue(OrderLogColumn.userId, OrderLogModel.getUserId())
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }
}
