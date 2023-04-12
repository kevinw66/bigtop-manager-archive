package org.apache.bigtop.manager.agent.utils.template;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PropertiesTemplate extends BaseTemplate {

    /**
     * Write properties file
     *
     * @param path   file path
     * @param configMap data map
     * @return flag
     */
    public void writeProperties(String path, Map<String, Object> configMap) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", configMap);

        super.writeTemplate(path, modelMap, "properties");
    }

    /**
     * Write properties file
     *
     * @param path   file path
     * @param configList data map
     * @return flag
     */
    public void writeProperties(String path, List<Map<String, Object>> configList) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("modelList", configList);

        super.writeTemplate(path, modelMap, "properties_list");
    }

}
