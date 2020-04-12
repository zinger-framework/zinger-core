package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.Column.*;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.logger.CollegeLogModel;
import com.food.ordering.zinger.constant.Query.CollegeQuery;
import com.food.ordering.zinger.rowMapperLambda.CollegeRowMapperLambda;
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
public class CollegeDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    AuditLogDao auditLogDao;

    public Response<String> insertCollege(CollegeModel collegeModel, RequestHeaderModel requestHeaderModel) {

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
                        .addValue(CollegeColumn.name, collegeModel.getName())
                        .addValue(CollegeColumn.address, collegeModel.getAddress())
                        .addValue(CollegeColumn.iconUrl, collegeModel.getIconUrl());

                int responseValue = namedParameterJdbcTemplate.update(CollegeQuery.insertCollege, parameters);
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

        auditLogDao.insertCollegeLog(new CollegeLogModel(response, requestHeaderModel.getMobile(), null, collegeModel.toString(), priority));
        return response;
    }

    public Response<List<CollegeModel>> getAllColleges(RequestHeaderModel requestHeaderModel) {

        Response<List<CollegeModel>> response = new Response<>();
        List<CollegeModel> list = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1002);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                try {
                    list = namedParameterJdbcTemplate.query(CollegeQuery.getAllColleges, CollegeRowMapperLambda.collegeRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(CDNA1102);
                    response.setMessage(CollegeDetailNotAvailable);
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

        auditLogDao.insertCollegeLog(new CollegeLogModel(response, requestHeaderModel.getMobile(), null, null, priority));
        return response;
    }

    public Response<CollegeModel> getCollegeById(Integer collegeId) {
        Response<CollegeModel> response = new Response<>();
        CollegeModel college = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(CollegeColumn.id, collegeId);

            try {
                college = namedParameterJdbcTemplate.queryForObject(CollegeQuery.getCollegeById, parameters, CollegeRowMapperLambda.collegeRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (college != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(college);
            }
        }

        return response;
    }
}
