package com.food.ordering.zinger.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.TransactionModel;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PaymentResponse provides the transaction response code and message.
 */
@Configuration
public class PaymentResponse {

    private String code;
    private String message;
    private String responseValue;

    /**
     * Parse the file "responseStatus.json".
     *
     * @return the string containing the entire json.
     * @implNote This file contains the transaction response code and
     * message from the payment gateway.
     * It is left completely free to EDIT for the developer convenience
     * with respect to the chosen payment gateway.
     * <p>
     * Add/remove transaction status in the above mentioned file matching
     * the TRANSACTION_STATUS enum.
     */
    @Bean
    public void parseResponseStatus() {
        try {
            List<String> list = Files.readAllLines(new File("src/main/resources/responseStatus.json").toPath());
            responseValue = list.stream().collect(Collectors.joining());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * If the above mentioned JSON file is edited,
     * make the changes here accordingly.
     *
     * @param transactionModel TransactionModel
     * @return the new order status for the given transaction status.
     */
    public Enums.OrderStatus getOrderStatus(TransactionModel transactionModel) {
        if (responseValue == null)
            return null;

        JsonParser parser = JsonParserFactory.getJsonParser();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonResponse = parser.parseMap(responseValue);

        for (Map.Entry<String, Object> entry : jsonResponse.entrySet()) {
            List<PaymentResponse> responseList = mapper.convertValue(entry.getValue(), new TypeReference<List<PaymentResponse>>() {
            });
            for (int i = 0; i < responseList.size(); i++) {
                PaymentResponse response = responseList.get(i);
                if (response.getCode().equals(transactionModel.getResponseCode()) && response.getMessage().equals(transactionModel.getResponseMessage())) {
                    Enums.TransactionStatus status = Enums.TransactionStatus.valueOf(entry.getKey());
                    return (status == Enums.TransactionStatus.TXN_SUCCESS) ? Enums.OrderStatus.PLACED : Enums.OrderStatus.valueOf(status.name());
                }
            }
        }
        return null;
    }

    /**
     * This is a helper method to get the transaction token from the payment gateway. The orderId and merchantId
     * are passed to the payment gateway
     *
     * @param orderModel OrderModel
     * @param merchantId String
     * @return token fetched from payment gateway is returned
     */
    public Response<String> initiateTransaction(OrderModel orderModel, String merchantId) {
        Response<String> response = new Response<>();

        Integer orderId = orderModel.getId();

        //TODO: Implement API to get Transaction Token From Payment Gateway using OrderId & merchantId
        //String transactionToken = getTransactionToken(orderId, merchantId);

        String transactionToken = "12Abdsfds";

        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);
        response.setData(transactionToken);
        return response;
    }

    /**
     * Helper method to get transaction Status from payment gateway
     *
     * @param orderId the order id
     * @return the latest transaction data from payment gateway is returned
     */
    public Response<TransactionModel> getTransactionStatus(Integer orderId) {
        Response<TransactionModel> transactionModelResponse = new Response<>();

        //TODO: GET Transaction Status from Payment Gateway
        TransactionModel transactionModel = new TransactionModel();

        //Populating Dummy Values Here
        transactionModel.setTransactionId("T" + orderId);
        transactionModel.setBankTransactionId("BT0001");
        transactionModel.transactionAmountSet(90.0);
        transactionModel.setCurrency("INR");
        transactionModel.setResponseCode("01");
        transactionModel.setResponseMessage("Success");
        transactionModel.setGatewayName("PAYTM");
        transactionModel.setBankName("HDFC");
        transactionModel.setPaymentMode("UPI");
        transactionModel.setChecksumHash("XXXXX");
        transactionModel.getOrderModel().setId(orderId);

        transactionModelResponse.setCode(ErrorLog.CodeSuccess);
        transactionModelResponse.setMessage(ErrorLog.Success);
        transactionModelResponse.setData(transactionModel);
        return transactionModelResponse;
    }

    /**
     * This method is used for fetching the refund status from the Payment gateway.
     *
     * @param orderId Integer
     * @return the latest transaction data from payment gateway is returned
     */
    public Response<TransactionModel> getRefundStatus(Integer orderId) {
        Response<TransactionModel> transactionModelResponse = new Response<>();

        //TODO: GET Transaction Status from Payment Gateway
        TransactionModel transactionModel = new TransactionModel();

        //Populating Dummy Values Here
        transactionModel.setTransactionId("T" + orderId);
        transactionModel.setBankTransactionId("BT0001");
        transactionModel.transactionAmountSet(90.0);
        transactionModel.setCurrency("INR");
        transactionModel.setResponseCode("03");
        transactionModel.setResponseMessage("Refund Completed");
        transactionModel.setGatewayName("PAYTM");
        transactionModel.setBankName("HDFC");
        transactionModel.setPaymentMode("UPI");
        transactionModel.setChecksumHash("XXXXX");
        transactionModel.getOrderModel().setId(orderId);

        transactionModelResponse.setCode(ErrorLog.CodeSuccess);
        transactionModelResponse.setMessage(ErrorLog.Success);
        transactionModelResponse.setData(transactionModel);
        return transactionModelResponse;
    }

    /**
     * This method is used to initiate refund of payment
     */
    public void initiateRefund() {
        //TODO: Initiate the refund using payment gateway
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
