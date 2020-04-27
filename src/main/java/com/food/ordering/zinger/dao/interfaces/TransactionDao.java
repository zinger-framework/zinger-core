package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;

public interface TransactionDao {
    Response<String> insertTransactionDetails(TransactionModel transactionModel);

    void updatePendingTransaction(TransactionModel transactionModel);
}
