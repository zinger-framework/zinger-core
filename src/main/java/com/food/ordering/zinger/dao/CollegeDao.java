package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.CollegeColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.CollegeLogModel;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.query.CollegeQuery;
import com.food.ordering.zinger.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class CollegeDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    public Response<String> insertCollege(CollegeModel collegeModel, String oauthId, String mobile, String role) {
        Response<String> response = new Response<>();

        try {
            if (!role.equals((UserRole.SUPER_ADMIN).name())) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(CollegeColumn.name, collegeModel.getName())
                    .addValue(CollegeColumn.address, collegeModel.getAddress())
                    .addValue(CollegeColumn.iconUrl, collegeModel.getIconUrl());

            int responseValue = namedParameterJdbcTemplate.update(CollegeQuery.insertCollege, parameters);
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

    public Response<List<CollegeModel>> getAllColleges(String oauthId, String mobile, String role) {
        Response<List<CollegeModel>> response = new Response<>();
        List<CollegeModel> list = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

            try {
                list = namedParameterJdbcTemplate.query(CollegeQuery.getAllColleges, CollegeRowMapperLambda.collegeRowMapperLambda);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
            }
        }
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
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
