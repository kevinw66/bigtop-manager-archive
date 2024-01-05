package org.apache.bigtop.manager.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class JsonUtils {
    public static final ObjectMapper OBJECTMAPPER;

    static {
        OBJECTMAPPER = new ObjectMapper();
        OBJECTMAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> void writeToFile(String fileName, T obj) {
        writeToFile(new File(fileName), obj);
    }

    public static <T> void writeToFile(File file, T obj) {
        try {
            OBJECTMAPPER.writeValue(file, obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromFile(String fileName) {
        return readFromFile(new File(fileName), new TypeReference<>() {});
    }

    public static <T> T readFromFile(String fileName, TypeReference<T> typeReference) {
        return readFromFile(new File(fileName), typeReference);
    }

    public static <T> T readFromFile(File file) {
        try {
            return OBJECTMAPPER.readValue(file, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromFile(File file, TypeReference<T> typeReference) {
        try {
            return OBJECTMAPPER.readValue(file, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromString(String json) {
        try {
            return OBJECTMAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromString(String json, TypeReference<T> typeReference) {
        try {
            return OBJECTMAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromString(String json, Class<T> clazz) {
        try {
            return OBJECTMAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(String filename) {
        try {
            return OBJECTMAPPER.readTree(new File(filename));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String writeAsString(T obj) {
        try {
            return OBJECTMAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
