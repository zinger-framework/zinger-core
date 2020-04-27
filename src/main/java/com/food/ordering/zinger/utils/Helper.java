package com.food.ordering.zinger.utils;

import com.food.ordering.zinger.model.OrderItemModel;

import java.util.List;

public class Helper {
    public static String toJsonFormattedString(List<String> stringList) {
        String result = "[";
        for (int i = 0; i < stringList.size(); i++) {
            result += "\"" + stringList.get(i) + "\"";
            if (i < stringList.size() - 1)
                result += ",";
        }
        result += "]";
        return result;
    }

    public static String toOrderItemJsonString(List<OrderItemModel> orderItemModelList) {
        String result = "[";
        for (int i = 0; i < orderItemModelList.size(); i++) {
            result += "{\"itemId\" :" + orderItemModelList.get(i).getItemModel().getId() + ",";
            result += "\"quantity\" :" + orderItemModelList.get(i).getQuantity() + "},";
        }
        result = result.substring(0, result.length() - 1) + "]";
        return result;
    }


    public static Boolean isNotNull(String string) {
        return string != null && string.length() > 0;
    }
}
