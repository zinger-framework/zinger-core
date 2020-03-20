package com.food.ordering.zinger.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.zinger.column.logger.CollegeLogColumn;
import com.food.ordering.zinger.column.logger.ConfigurationLogColumn;
import com.food.ordering.zinger.column.logger.ItemLogColumn;
import com.food.ordering.zinger.column.logger.OrderLogColumn;
import com.food.ordering.zinger.column.logger.ShopLogColumn;
import com.food.ordering.zinger.column.logger.TransactionLogColumn;
import com.food.ordering.zinger.column.logger.UserCollegeLogColumn;
import com.food.ordering.zinger.column.logger.UserLogColumn;
import com.food.ordering.zinger.column.logger.UserShopLogColumn;
import com.food.ordering.zinger.model.logger.CollegeLogModel;
import com.food.ordering.zinger.model.logger.ConfigurationLogModel;
import com.food.ordering.zinger.model.logger.ItemLogModel;
import com.food.ordering.zinger.model.logger.OrderLogModel;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import com.food.ordering.zinger.model.logger.TransactionLogModel;
import com.food.ordering.zinger.model.logger.UserCollegeLogModel;
import com.food.ordering.zinger.model.logger.UserLogModel;
import com.food.ordering.zinger.model.logger.UserShopLogModel;
import com.food.ordering.zinger.query.AuditLogQuery;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;

@Repository
public class AuditLogDao {
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	UtilsDao utilsDao;

	public Response<String> insertCollegeLog(CollegeLogModel collegeLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(CollegeLogColumn.id, collegeLogModel.getId())
					.addValue(CollegeLogColumn.errorCode, collegeLogModel.getErrorCode())
					.addValue(CollegeLogColumn.mobile, collegeLogModel.getMobile())
					.addValue(CollegeLogColumn.message, collegeLogModel.getMessage())
					.addValue(CollegeLogColumn.updatedValue, collegeLogModel.getUpdatedValue())
					.addValue(CollegeLogColumn.date, collegeLogModel.getDate())
					.addValue(CollegeLogColumn.priority, collegeLogModel.getPriority());

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

			SqlParameterSource parameters = new MapSqlParameterSource().addValue(ShopLogColumn.id, ShopLogModel.getId())
					.addValue(ShopLogColumn.errorCode, ShopLogModel.getErrorCode())
					.addValue(ShopLogColumn.mobile, ShopLogModel.getMobile())
					.addValue(ShopLogColumn.message, ShopLogModel.getMessage())
					.addValue(ShopLogColumn.updatedValue, ShopLogModel.getUpdatedValue())
					.addValue(ShopLogColumn.date, ShopLogModel.getDate())
					.addValue(ShopLogColumn.priority, ShopLogModel.getPriority());

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
					.addValue(UserLogColumn.date, UserLogModel.getDate())
					.addValue(UserLogColumn.priority, UserLogModel.getPriority());

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

			SqlParameterSource parameters = new MapSqlParameterSource().addValue(ItemLogColumn.id, ItemLogModel.getId())
					.addValue(ItemLogColumn.errorCode, ItemLogModel.getErrorCode())
					.addValue(ItemLogColumn.mobile, ItemLogModel.getMobile())
					.addValue(ItemLogColumn.message, ItemLogModel.getMessage())
					.addValue(ItemLogColumn.updatedValue, ItemLogModel.getUpdatedValue())
					.addValue(ItemLogColumn.date, ItemLogModel.getDate())
					.addValue(ItemLogColumn.priority, ItemLogModel.getPriority());

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

	public Response<String> insertTransactionLog(TransactionLogModel TransactionLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(TransactionLogColumn.transactionId, TransactionLogModel.getTransactionId())
					.addValue(TransactionLogColumn.errorCode, TransactionLogModel.getErrorCode())
					.addValue(TransactionLogColumn.mobile, TransactionLogModel.getMobile())
					.addValue(TransactionLogColumn.message, TransactionLogModel.getMessage())
					.addValue(TransactionLogColumn.updatedValue, TransactionLogModel.getUpdatedValue())
					.addValue(TransactionLogColumn.date, TransactionLogModel.getDate())
					.addValue(TransactionLogColumn.priority, TransactionLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertTransactionLog, parameters);
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
					.addValue(OrderLogColumn.date, OrderLogModel.getDate())
					.addValue(OrderLogColumn.priority, OrderLogModel.getPriority());

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

	public Response<String> insertUserShopLog(UserShopLogModel UserShopLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(UserShopLogColumn.usersMobile, UserShopLogModel.getUsersMobile())
					.addValue(UserShopLogColumn.errorCode, UserShopLogModel.getErrorCode())
					.addValue(UserShopLogColumn.mobile, UserShopLogModel.getMobile())
					.addValue(UserShopLogColumn.message, UserShopLogModel.getMessage())
					.addValue(UserShopLogColumn.updatedValue, UserShopLogModel.getUpdatedValue())
					.addValue(UserShopLogColumn.date, UserShopLogModel.getDate())
					.addValue(UserShopLogColumn.priority, UserShopLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertUserShopLog, parameters);
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

	public Response<String> insertUserCollegeLog(UserCollegeLogModel UserCollegeLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(UserCollegeLogColumn.usersMobile, UserCollegeLogModel.getUsersMobile())
					.addValue(UserCollegeLogColumn.errorCode, UserCollegeLogModel.getErrorCode())
					.addValue(UserCollegeLogColumn.mobile, UserCollegeLogModel.getMobile())
					.addValue(UserCollegeLogColumn.message, UserCollegeLogModel.getMessage())
					.addValue(UserCollegeLogColumn.updatedValue, UserCollegeLogModel.getUpdatedValue())
					.addValue(UserCollegeLogColumn.date, UserCollegeLogModel.getDate())
					.addValue(UserCollegeLogColumn.priority, UserCollegeLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertUserCollegeLog, parameters);
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

	public Response<String> insertConfigurationLog(ConfigurationLogModel ConfigurationLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(ConfigurationLogColumn.shopId, ConfigurationLogModel.getShopId())
					.addValue(ConfigurationLogColumn.errorCode, ConfigurationLogModel.getErrorCode())
					.addValue(ConfigurationLogColumn.mobile, ConfigurationLogModel.getMobile())
					.addValue(ConfigurationLogColumn.message, ConfigurationLogModel.getMessage())
					.addValue(ConfigurationLogColumn.updatedValue, ConfigurationLogModel.getUpdatedValue())
					.addValue(ConfigurationLogColumn.date, ConfigurationLogModel.getDate())
					.addValue(ConfigurationLogColumn.priority, ConfigurationLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertConfigurationLog, parameters);
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
