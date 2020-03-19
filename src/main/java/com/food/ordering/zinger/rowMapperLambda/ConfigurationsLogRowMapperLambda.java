package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.ConfigurationsLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.ConfigurationsLogColumn.*;

public class ConfigurationsLogRowMapperLambda {
	 public static final RowMapper<ConfigurationsLogModel> configurationsLogRowMapperLambda = (rs, rownum) -> {
	        ConfigurationsLogModel configuration = new ConfigurationsLogModel();
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