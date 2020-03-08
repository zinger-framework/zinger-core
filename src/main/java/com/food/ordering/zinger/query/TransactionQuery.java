package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.TransactionColumn.*;

public class TransactionQuery {
    public static final String insertTransaction = "INSERT INTO " + tableName + " (" + transactionId + ", " + bankTransactionId + ", " + currency + ", " + responseCode + ", " + responseMessage + ", " + gatewayName + ", " + bankName + ", " + paymentMode + ", " + checksumHash + ") VALUES(:" + transactionId + ", :" + bankTransactionId + ", :" + currency + ", :" + responseCode + ", :" + responseMessage + ", :" + gatewayName + ", :" + bankName + ", :" + paymentMode + ", :" + checksumHash + ")";

    public static final String getTransaction = "SELECT " + transactionId + ", " + bankTransactionId + ", " + currency + ", " + responseCode + ", " + responseMessage + ", " + gatewayName + ", " + bankName + ", " + paymentMode + ", " + checksumHash + " WHERE " + transactionId + " = :" + transactionId;

    public static final String updateTransaction = "UPDATE " + tableName + " SET " + responseCode + " = :" + responseCode + ", " + responseMessage + " = :" + responseMessage + ", " + date + " = CURRENT_TIMESTAMP" + " WHERE " + transactionId + " = :" + transactionId;
}
