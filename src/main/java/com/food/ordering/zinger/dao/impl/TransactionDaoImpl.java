package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.TransactionColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.TransactionQuery;
import com.food.ordering.zinger.dao.interfaces.TransactionDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.food.ordering.zinger.constant.Column.TransactionColumn.*;

/**
 * TransactionDao is responsible for CRUD operations in
 * Transaction table in MySQL.
 */
@Repository
public class TransactionDaoImpl implements TransactionDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * This is a helper method to insert transaction details in the transaction table
     *
     * @param transactionModel TransactionModel
     * @return If insertion is successful then success response is returned else failure response
     */
    @Override
    public Response<String> insertTransactionDetails(TransactionModel transactionModel) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(TransactionColumn.transactionId, transactionModel.getTransactionId())
                    .addValue(TransactionColumn.orderId, transactionModel.getOrderModel().getId())
                    .addValue(TransactionColumn.bankTransactionId, transactionModel.getBankTransactionId())
                    .addValue(TransactionColumn.currency, transactionModel.getCurrency())
                    .addValue(TransactionColumn.responseCode, transactionModel.getResponseCode())
                    .addValue(TransactionColumn.responseMessage, transactionModel.getResponseMessage())
                    .addValue(TransactionColumn.gatewayName, transactionModel.getGatewayName())
                    .addValue(TransactionColumn.bankName, transactionModel.getBankName())
                    .addValue(TransactionColumn.paymentMode, transactionModel.getPaymentMode())
                    .addValue(TransactionColumn.checksumHash, transactionModel.getChecksumHash());

            int transactionResult = namedParameterJdbcTemplate.update(TransactionQuery.insertTransaction, parameter);

            if (transactionResult > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * This is a helper method to update transaction details in the transaction table
     *
     * @param transactionModel TransactionModel
     * @return If insertion is successful then success response is returned else failure response
     */
    @Override
    public void updatePendingTransaction(TransactionModel transactionModel) {
        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(responseCode, transactionModel.getResponseCode())
                    .addValue(responseMessage, transactionModel.getResponseMessage())
                    .addValue(transactionId, transactionModel.getTransactionId());

            namedParameterJdbcTemplate.update(TransactionQuery.updateTransaction, parameter);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
