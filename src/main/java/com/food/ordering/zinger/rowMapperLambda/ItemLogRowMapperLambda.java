package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.ItemLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.ItemLogColumn.*;

public class ItemLogRowMapperLambda {
	 public static final RowMapper<ItemLogModel> itemLogRowMapperLambda = (rs, rownum) -> {
	        ItemLogModel item = new ItemLogModel();
	        item.setId(rs.getInt(id));
	        item.setErrorCode(rs.getInt(errorCode));
	        item.setMobile(rs.getString(mobile));
	        item.setMessage(rs.getString(message));
	        item.setUpdatedValue(rs.getString(updatedValue));
	        item.setDate(rs.getTimestamp(date));
	        item.setPriority(Priority.valueOf(rs.getString(priority)));
	        return item;
	    };
}