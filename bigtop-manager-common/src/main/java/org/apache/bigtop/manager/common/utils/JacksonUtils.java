package org.apache.bigtop.manager.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils {
    public static final ObjectMapper OBJECTMAPPER;

    static {
        OBJECTMAPPER = new ObjectMapper();
        OBJECTMAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
