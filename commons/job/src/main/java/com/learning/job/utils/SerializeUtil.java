package com.learning.job.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeUtil {
    private static Logger logger = LoggerFactory.getLogger(SerializeUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public SerializeUtil() {
    }

    public static <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception var2) {
            Exception e = var2;
            logger.error("jackson param serialize format error : {}", e.toString());
            return null;
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            T t = objectMapper.readValue(bytes, clazz);
            return t;
        } catch (Exception var3) {
            Exception e = var3;
            logger.error("jackson param deserialize format error : {}", e.toString());
            return null;
        }
    }
}