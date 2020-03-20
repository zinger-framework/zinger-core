package com.food.ordering.zinger.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.zinger.column.CollegeLogColumn;
import com.food.ordering.zinger.column.ConfigurationsLogColumn;
import com.food.ordering.zinger.column.ItemLogColumn;
import com.food.ordering.zinger.column.OrdersLogColumn;
import com.food.ordering.zinger.column.ShopLogColumn;
import com.food.ordering.zinger.column.TransactionsLogColumn;
import com.food.ordering.zinger.column.UsersCollegeLogColumn;
import com.food.ordering.zinger.column.UsersLogColumn;
import com.food.ordering.zinger.column.UsersShopLogColumn;
import com.food.ordering.zinger.model.CollegeLogModel;
import com.food.ordering.zinger.model.ConfigurationsLogModel;
import com.food.ordering.zinger.model.ItemLogModel;
import com.food.ordering.zinger.model.OrdersLogModel;
import com.food.ordering.zinger.model.ShopLogModel;
import com.food.ordering.zinger.model.TransactionsLogModel;
import com.food.ordering.zinger.model.UsersCollegeLogModel;
import com.food.ordering.zinger.model.UsersLogModel;
import com.food.ordering.zinger.model.UsersShopLogModel;
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

	public Response<String> insertUsersLog(UsersLogModel UsersLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(UsersLogColumn.usersMobile, UsersLogModel.getUsersMobile())
					.addValue(UsersLogColumn.errorCode, UsersLogModel.getErrorCode())
					.addValue(UsersLogColumn.mobile, UsersLogModel.getMobile())
					.addValue(UsersLogColumn.message, UsersLogModel.getMessage())
					.addValue(UsersLogColumn.updatedValue, UsersLogModel.getUpdatedValue())
					.addValue(UsersLogColumn.date, UsersLogModel.getDate())
					.addValue(UsersLogColumn.priority, UsersLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertUsersLog, parameters);
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

	public Response<String> insertTransactionsLog(TransactionsLogModel TransactionsLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(TransactionsLogColumn.transactionId, TransactionsLogModel.getTransactionId())
					.addValue(TransactionsLogColumn.errorCode, TransactionsLogModel.getErrorCode())
					.addValue(TransactionsLogColumn.mobile, TransactionsLogModel.getMobile())
					.addValue(TransactionsLogColumn.message, TransactionsLogModel.getMessage())
					.addValue(TransactionsLogColumn.updatedValue, TransactionsLogModel.getUpdatedValue())
					.addValue(TransactionsLogColumn.date, TransactionsLogModel.getDate())
					.addValue(TransactionsLogColumn.priority, TransactionsLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertTransactionsLog, parameters);
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

	public Response<String> insertOrdersLog(OrdersLogModel OrdersLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(OrdersLogColumn.id, OrdersLogModel.getId())
					.addValue(OrdersLogColumn.errorCode, OrdersLogModel.getErrorCode())
					.addValue(OrdersLogColumn.mobile, OrdersLogModel.getMobile())
					.addValue(OrdersLogColumn.message, OrdersLogModel.getMessage())
					.addValue(OrdersLogColumn.updatedValue, OrdersLogModel.getUpdatedValue())
					.addValue(OrdersLogColumn.date, OrdersLogModel.getDate())
					.addValue(OrdersLogColumn.priority, OrdersLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertOrdersLog, parameters);
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

	public Response<String> insertUsersShopLog(UsersShopLogModel UsersShopLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(UsersShopLogColumn.usersMobile, UsersShopLogModel.getUsersMobile())
					.addValue(UsersShopLogColumn.errorCode, UsersShopLogModel.getErrorCode())
					.addValue(UsersShopLogColumn.mobile, UsersShopLogModel.getMobile())
					.addValue(UsersShopLogColumn.message, UsersShopLogModel.getMessage())
					.addValue(UsersShopLogColumn.updatedValue, UsersShopLogModel.getUpdatedValue())
					.addValue(UsersShopLogColumn.date, UsersShopLogModel.getDate())
					.addValue(UsersShopLogColumn.priority, UsersShopLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertUsersShopLog, parameters);
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

	public Response<String> insertUsersCollegeLog(UsersCollegeLogModel UsersCollegeLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(UsersCollegeLogColumn.usersMobile, UsersCollegeLogModel.getUsersMobile())
					.addValue(UsersCollegeLogColumn.errorCode, UsersCollegeLogModel.getErrorCode())
					.addValue(UsersCollegeLogColumn.mobile, UsersCollegeLogModel.getMobile())
					.addValue(UsersCollegeLogColumn.message, UsersCollegeLogModel.getMessage())
					.addValue(UsersCollegeLogColumn.updatedValue, UsersCollegeLogModel.getUpdatedValue())
					.addValue(UsersCollegeLogColumn.date, UsersCollegeLogModel.getDate())
					.addValue(UsersCollegeLogColumn.priority, UsersCollegeLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertUsersCollegeLog, parameters);
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

	public Response<String> insertConfigurationsLog(ConfigurationsLogModel ConfigurationsLogModel) {
		Response<String> response = new Response<>();

		try {

			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue(ConfigurationsLogColumn.shopId, ConfigurationsLogModel.getShopId())
					.addValue(ConfigurationsLogColumn.errorCode, ConfigurationsLogModel.getErrorCode())
					.addValue(ConfigurationsLogColumn.mobile, ConfigurationsLogModel.getMobile())
					.addValue(ConfigurationsLogColumn.message, ConfigurationsLogModel.getMessage())
					.addValue(ConfigurationsLogColumn.updatedValue, ConfigurationsLogModel.getUpdatedValue())
					.addValue(ConfigurationsLogColumn.date, ConfigurationsLogModel.getDate())
					.addValue(ConfigurationsLogColumn.priority, ConfigurationsLogModel.getPriority());

			int responseValue = namedParameterJdbcTemplate.update(AuditLogQuery.insertConfigurationsLog, parameters);
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
