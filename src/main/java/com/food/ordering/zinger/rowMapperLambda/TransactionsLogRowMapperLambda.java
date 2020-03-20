package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.TransactionsLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.TransactionsLogColumn.*;

public class TransactionsLogRowMapperLambda {

	 public static final RowMapper<TransactionsLogModel> transactionsLogRowMapperLambda = (rs, rownum) -> {
	        TransactionsLogModel transactions = new TransactionsLogModel();
	        transactions.setTransactionId(rs.getString(transactionId));
	        transactions.setErrorCode(rs.getInt(errorCode));
	        transactions.setMobile(rs.getString(mobile));
	        transactions.setMessage(rs.getString(message));
	        transactions.setUpdatedValue(rs.getString(updatedValue));
	        transactions.setDate(rs.getTimestamp(date));
	        transactions.setPriority(Priority.valueOf(rs.getString(priority)));
	        return transactions;
	    };
}
