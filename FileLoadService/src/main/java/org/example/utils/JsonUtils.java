package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка преобразования в JSON", e);
        }
    }

    public static <T> T parseJson(InputStream is, Class<T> type) {
        try {
            return mapper.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка парсинга JSON", e);
        }
    }
}
