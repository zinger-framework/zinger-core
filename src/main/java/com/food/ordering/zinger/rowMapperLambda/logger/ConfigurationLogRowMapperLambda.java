package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.logger.ConfigurationLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.logger.ConfigurationLogColumn.*;

public class ConfigurationLogRowMapperLambda {
	 public static final RowMapper<ConfigurationLogModel> configurationsLogRowMapperLambda = (rs, rownum) -> {
	        ConfigurationLogModel configuration = new ConfigurationLogModel();
	        configuration.setShopId(rs.getInt(shopId));
	        configuration.setErrorCode(rs.getInt(errorCode));
	        configuration.setMobile(rs.getString(mobile));
	        configuration.setMessage(rs.getString(message));
	        configuration.setUpdatedValue(rs.getString(updatedValue));
	        configuration.setDate(rs.getTimestamp(date));
	        configuration.setPriority(Priority.valueOf(rs.getString(priority)));
	        return configuration;
	    };
}
