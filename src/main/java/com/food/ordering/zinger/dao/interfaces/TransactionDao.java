package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;

public interface TransactionDao {
    Response<String> insertTransactionDetails(TransactionModel transactionModel);

    Response<TransactionModel> getTransactionByOrderId(Integer orderId);

    void updatePendingTransaction(TransactionModel transactionModel);
}
