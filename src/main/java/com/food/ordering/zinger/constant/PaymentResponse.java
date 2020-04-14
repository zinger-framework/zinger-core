package com.food.ordering.zinger.constant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Configuration
public class PaymentResponse {

    private String code;
    private String message;

    @Bean
    public String parseResponseStatus() {
        try {
            List<String> list = Files.readAllLines(new File("src/main/resources/responseStatus.json").toPath());
            return list.stream().collect(Collectors.joining());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return null;
    }

    public Enums.OrderStatus getOrderStatus(TransactionModel transactionModel) {
        String responseValue = parseResponseStatus();
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
