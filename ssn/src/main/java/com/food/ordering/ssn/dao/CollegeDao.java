package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.query.CollegeQuery;
import com.food.ordering.ssn.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class CollegeDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	UtilsDao utilsDao;

	public Response<List<CollegeModel>> getAllColleges(String oauthId) {
		Response<List<CollegeModel>> response = new Response<>();
		List<CollegeModel> list = null;

		try {
			if (utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;

			list = jdbcTemplate.query(CollegeQuery.getAllColleges, CollegeRowMapperLambda.collegeRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (list != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(list);
			}
		}
		return response;
	}

	public Response<CollegeModel> getCollegeById(Integer collegeId, String oauthIdRh) {
		CollegeModel college = null;
		Response<CollegeModel> response = new Response<>();

		try {

			if (utilsDao.validateUser(oauthIdRh).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", collegeId);
			college = jdbcTemplate.queryForObject(CollegeQuery.getCollegeById, parameters,
					CollegeRowMapperLambda.collegeRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (college != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(college);
			}
		}
		return response;
	}

	public Response<CollegeModel> updateCollege(String oauthId, Integer collegeId, CollegeModel newCollege) {
		Response<CollegeModel> response = new Response();

		try {

			if (utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", newCollege.getName())
					.addValue("icon_url", newCollege.getIconUrl()).addValue("address", newCollege.getAddress())
					.addValue("id", collegeId);

			jdbcTemplate.update(CollegeQuery.updateCollege, parameters);

			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
			response.setData(newCollege);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
