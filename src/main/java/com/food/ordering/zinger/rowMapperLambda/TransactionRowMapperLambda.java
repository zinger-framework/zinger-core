package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.TransactionModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.TransactionColumn.*;

public class TransactionRowMapperLambda {
    public static final RowMapper<TransactionModel> transactionRowMapperLambda = (rs, rownum) -> {
        TransactionModel transactionModel = new TransactionModel();

        OrderModel orderModel = new OrderModel();
        orderModel.setId(rs.getString(orderId));
        transactionModel.setOrderModel(orderModel);

        transactionModel.setOrderId(rs.getString(orderId));
        transactionModel.setTransactionId(rs.getString(transactionId));
        transactionModel.setBankTransactionId(rs.getString(bankTransactionId));
        transactionModel.setCurrency(rs.getString(currency));
        transactionModel.setResponseCode(rs.getString(responseCode));
        transactionModel.setResponseMessage(rs.getString(responseMessage));
        transactionModel.setGatewayName(rs.getString(gatewayName));
        transactionModel.setBankName(rs.getString(bankName));
        transactionModel.setPaymentMode(rs.getString(paymentMode));
        transactionModel.setChecksumHash(rs.getString(checksumHash));
        return transactionModel;
    };
}
