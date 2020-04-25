package com.food.ordering.zinger.utils;

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

    public static Boolean isNotNull(String string) {
        return string != null && string.length() > 0;
    }
}
