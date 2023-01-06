package com.dail.starter.cache.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * description object工具类
 *
 * @author Dail 2023/01/06 14:47
 */
public class BaseObjectUtil {

    private BaseObjectUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseObjectUtil.class);

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Date.class, new DateSerializer());
        javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
        OBJECT_MAPPER.registerModule(javaTimeModule);
        // 如果遇到未知属性时,不抛出异常
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 允许反序列化key值不带引号的情况
        OBJECT_MAPPER.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        // 忽略无法转换的对象
        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }

    public static <T> String toJson(T object) {
        if (object == null) {
            return "";
        } else if (!(object instanceof Integer) && !(object instanceof Long) && !(object instanceof Float)
                && !(object instanceof Double) && !(object instanceof Boolean) && !(object instanceof String)) {
            try {
                return getInstance().writeValueAsString(object);
            } catch (JsonProcessingException ex) {
                LOGGER.error("object to json error. object:{}. error message:{}.", object, ex.getMessage());
                return "";
            }
        } else {
            return String.valueOf(object);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json != null && json.length() != 0 && clazz != null) {
            try {
                return getInstance().readValue(json, clazz);
            } catch (JsonProcessingException ex) {
                LOGGER.error("json to object clazz error. json:{}. error message:{}.", json, ex.getMessage());
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        if (json != null && json.length() != 0 && valueTypeRef != null) {
            try {
                return getInstance().readValue(json, valueTypeRef);
            } catch (JsonProcessingException ex) {
                LOGGER.error("json to object ref error. json:{}. error message:{}.", json, ex.getMessage());
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        JavaType javaType = getInstance().getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return getInstance().readValue(json, javaType);
        } catch (JsonProcessingException ex) {
            LOGGER.error("json to object List error. json:{}. error message:{}.", json, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static class DateSerializer extends JsonSerializer<Date> {
        final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        private DateSerializer() {
        }

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializers)
                throws IOException {
            jsonGenerator.writeString(this.dateFormat.format(date));
        }
    }

    public static class DateDeserializer extends JsonDeserializer<Date> {
        final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        private DateDeserializer() {
        }

        @Override
        public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            try {
                return this.dateFormat.parse(jsonParser.getValueAsString());
            } catch (ParseException var4) {
                LOGGER.error("date format error : {}", ExceptionUtils.getStackTrace(var4));
                return null;
            }
        }
    }

    /**
     * Object转换为List
     *
     * @param obj   数据
     * @param clazz 类
     * @param <T>   泛型
     * @return 数据
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return Collections.emptyList();
    }
}
