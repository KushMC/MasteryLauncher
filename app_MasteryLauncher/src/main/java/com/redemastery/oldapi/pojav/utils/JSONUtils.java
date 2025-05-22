package com.redemastery.oldapi.pojav.utils;

import java.util.Map;

public class JSONUtils {
    public static String[] insertJSONValueList(String[] args, Map<String, String> keyValueMap) {
        for (int i = 0; i < args.length; i++) {
            args[i] = insertSingleJSONValue(args[i], keyValueMap);
        }
        return args;
    }

    public static String insertSingleJSONValue(String value, Map<String, String> keyValueMap) {
        if (value == null || keyValueMap == null) return value;

        String result = value;
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (key == null) continue;
            result = result.replace("${" + key + "}", val != null ? val : "");
        }
        return result;
    }
}
