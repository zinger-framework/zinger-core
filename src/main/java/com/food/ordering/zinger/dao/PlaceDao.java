package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.Column.*;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.logger.PlaceLogModel;
import com.food.ordering.zinger.constant.Query.PlaceQuery;
import com.food.ordering.zinger.rowMapperLambda.PlaceRowMapperLambda;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;

@Repository
public class PlaceDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    AuditLogDao auditLogDao;

    public Response<String> insertPlace(PlaceModel placeModel, RequestHeaderModel requestHeaderModel) {

        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (!requestHeaderModel.getRole().equals((UserRole.SUPER_ADMIN).name())) {
                response.setCode(ErrorLog.IH1000);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1001);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(PlaceColumn.name, placeModel.getName())
                        .addValue(PlaceColumn.address, placeModel.getAddress())
                        .addValue(PlaceColumn.iconUrl, placeModel.getIconUrl());

                int responseValue = namedParameterJdbcTemplate.update(PlaceQuery.insertPlace, parameters);
                if (responseValue > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(CDNU1100);
                }
            }
        } catch (Exception e) {
            response.setCode(CE1101);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertPlaceLog(new PlaceLogModel(response, requestHeaderModel.getMobile(), null, placeModel.toString(), priority));
        return response;
    }

    public Response<List<PlaceModel>> getAllPlaces(RequestHeaderModel requestHeaderModel) {

        Response<List<PlaceModel>> response = new Response<>();
        List<PlaceModel> list = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1002);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                try {
                    list = namedParameterJdbcTemplate.query(PlaceQuery.getAllPlaces, PlaceRowMapperLambda.placeRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(CDNA1102);
                    response.setMessage(PlaceDetailNotAvailable);
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            response.setCode(CE1103);
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
                priority = Priority.LOW;
            }
        }

        auditLogDao.insertPlaceLog(new PlaceLogModel(response, requestHeaderModel.getMobile(), null, null, priority));
        return response;
    }

    public Response<PlaceModel> getPlaceById(Integer placeId) {
        Response<PlaceModel> response = new Response<>();
        PlaceModel place = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(PlaceColumn.id, placeId);

            try {
                place = namedParameterJdbcTemplate.queryForObject(PlaceQuery.getPlaceById, parameters, PlaceRowMapperLambda.placeRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (place != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(place);
            }
        }

        return response;
    }
}
