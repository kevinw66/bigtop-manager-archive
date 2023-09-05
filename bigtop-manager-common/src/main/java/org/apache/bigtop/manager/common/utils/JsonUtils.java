package org.apache.bigtop.manager.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
public class JsonUtils {
    public static final ObjectMapper OBJECTMAPPER;

    static {
        OBJECTMAPPER = new ObjectMapper();
        OBJECTMAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Generate json file
     *
     * @param fileName json file
     * @param object   json content
     */
    public static void writeJson(String fileName, Object object) {
        try {
            JsonUtils.OBJECTMAPPER.writeValue(new File(fileName), object);
        } catch (Exception e) {
            log.error(MessageFormat.format("Write Json {0} error, ", fileName), e);
        }
    }

    public static <T> T readJson(String fileName, TypeReference<T> typeReference) {
        try {
            return JsonUtils.OBJECTMAPPER.readValue(new File(fileName), typeReference);
        } catch (Exception e) {
            log.error(MessageFormat.format("Read Json {0} error, ", fileName), e);
        }
        return null;
    }

    public static <T> T string2Json(String jsonStr, Class<T> clazz) {
        try {
            return JsonUtils.OBJECTMAPPER.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.error(MessageFormat.format("string2Json {0} error, ", jsonStr), e);
        }
        return null;
    }

    public static <T> T string2Json(String jsonStr, TypeReference<T> typeReference) {
        try {
            return JsonUtils.OBJECTMAPPER.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            log.error(MessageFormat.format("string2Json {0} error, ", jsonStr), e);
        }
        return null;
    }

    public static String object2String(Object object) {
        try {
            return JsonUtils.OBJECTMAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error(MessageFormat.format("object2String {0} error, ", object), e);
        }
        return null;
    }
}
