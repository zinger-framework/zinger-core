package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.TransactionColumn;
import com.food.ordering.ssn.model.TransactionModel;
import com.food.ordering.ssn.query.TransactionQuery;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public Response<String> insertTransactionDetails(TransactionModel transactionModel) {


        Response<String> response = new Response<>();

        try {

            MapSqlParameterSource parameter = new MapSqlParameterSource().addValue(TransactionColumn.transactionId, transactionModel.getTransactionId())
                    .addValue(TransactionColumn.currency, transactionModel.getCurrency())
                    .addValue(TransactionColumn.responseCode, transactionModel.getResponseCode())
                    .addValue(TransactionColumn.responseMessage, transactionModel.getResponseMessage())
                    .addValue(TransactionColumn.gatewayName, transactionModel.getGatewayName())
                    .addValue(TransactionColumn.bankName, transactionModel.getBankName())
                    .addValue(TransactionColumn.paymentMode, transactionModel.getPaymentMode())
                    .addValue(TransactionColumn.checksumHash, transactionModel.getChecksumHash());

            int transactionResult = jdbcTemplate.update(TransactionQuery.insertTransaction, parameter);

            if (transactionResult > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return response;

    }

}
