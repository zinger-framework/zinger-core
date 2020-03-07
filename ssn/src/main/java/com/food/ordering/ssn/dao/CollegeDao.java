package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.CollegeColumn;
import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.query.CollegeQuery;
import com.food.ordering.ssn.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CollegeDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    public Response<List<CollegeModel>> getAllColleges(String oauthId, String mobile) {
        Response<List<CollegeModel>> response = new Response<>();
        List<CollegeModel> list = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            list = namedParameterJdbcTemplate.query(CollegeQuery.getAllColleges, CollegeRowMapperLambda.collegeRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
            }
        }
        return response;
    }

    public Response<CollegeModel> getCollegeById(Integer collegeId, String oauthIdRh, String mobile) {
        Response<CollegeModel> response = new Response<>();
        CollegeModel college = null;

        try {
            if (!utilsDao.validateUser(oauthIdRh, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(CollegeColumn.id, collegeId);
            college = namedParameterJdbcTemplate.queryForObject(CollegeQuery.getCollegeById, parameters, CollegeRowMapperLambda.collegeRowMapperLambda);
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

    public Response<String> updateCollege(CollegeModel collegeModel, String oauthId, String mobile) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(oauthId, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(CollegeColumn.name, collegeModel.getName())
                    .addValue(CollegeColumn.iconUrl, collegeModel.getIconUrl())
                    .addValue(CollegeColumn.address, collegeModel.getAddress());

            int result = namedParameterJdbcTemplate.update(CollegeQuery.updateCollege, parameters);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
