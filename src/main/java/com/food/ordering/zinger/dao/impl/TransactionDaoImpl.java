package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.TransactionColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.TransactionQuery;
import com.food.ordering.zinger.dao.interfaces.TransactionDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;
import com.food.ordering.zinger.rowMapperLambda.TransactionRowMapperLambda;
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
     * This is a helper method to get transaction details in the transaction table by orderId
     *
     * @param orderId Integer
     * @return If transaction with given orderId exists then corresponding transaction data is returned
     */
    @Override
    public Response<TransactionModel> getTransactionByOrderId(Integer orderId) {
        Response<TransactionModel> response = new Response<>();
        TransactionModel transactionModel = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(TransactionColumn.orderId, orderId);

            try {
                transactionModel = namedParameterJdbcTemplate.queryForObject(TransactionQuery.getTransactionByOrderId, parameter, TransactionRowMapperLambda.transactionRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            if (transactionModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(transactionModel);
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
                    .addValue(orderId, transactionModel.getTransactionId());

            namedParameterJdbcTemplate.update(TransactionQuery.updateTransaction, parameter);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
