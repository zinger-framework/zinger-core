package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.TransactionColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.TransactionQuery;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;
import com.food.ordering.zinger.rowMapperLambda.TransactionRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.food.ordering.zinger.constant.Column.TransactionColumn.*;

public interface TransactionDao {
    Response<String> insertTransactionDetails(TransactionModel transactionModel);

    Response<TransactionModel> getTransactionByOrderId(Integer orderId);

    void updatePendingTransaction(TransactionModel transactionModel);
}
