package org.apache.bigtop.manager.stack.common.utils.template;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PropertiesTemplate extends BaseTemplate {

    /**
     * Write properties file
     *
     * @param path      file path
     * @param configMap data map
     * @return flag
     */
    public static void writeProperties(String path, Map<String, Object> configMap) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", configMap);

        writeTemplate(path, modelMap, "properties");
    }

    /**
     * Write properties file
     *
     * @param path       file path
     * @param configList data map
     * @return flag
     */
    public static void writeProperties(String path, List<Map<String, Object>> configList) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("modelList", configList);

        writeTemplate(path, modelMap, "properties_list");
    }

}