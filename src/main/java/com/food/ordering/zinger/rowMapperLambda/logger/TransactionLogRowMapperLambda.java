package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.logger.TransactionLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.logger.TransactionLogColumn.*;

public class TransactionLogRowMapperLambda {

	 public static final RowMapper<TransactionLogModel> transactionsLogRowMapperLambda = (rs, rownum) -> {
	        TransactionLogModel transactions = new TransactionLogModel();
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
