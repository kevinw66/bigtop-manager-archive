package org.apache.bigtop.manager.stack.common.utils;


import org.apache.bigtop.manager.common.utils.JacksonUtils;
import org.apache.bigtop.manager.stack.common.exception.StackException;

import java.io.File;
import java.io.IOException;

public class JsonUtils {

    /**
     * Generate json file
     *
     * @param fileName json file
     * @param object   json content
     */
    public static void writeJson(String fileName, Object object) {
        try {
            JacksonUtils.OBJECTMAPPER.writeValue(new File(fileName), object);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }
}
