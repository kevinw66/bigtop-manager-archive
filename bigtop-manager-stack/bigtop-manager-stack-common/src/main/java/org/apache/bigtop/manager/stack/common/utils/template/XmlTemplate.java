package org.apache.bigtop.manager.stack.common.utils.template;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class XmlTemplate extends BaseTemplate {

    /**
     * Write xml file
     *
     * @param path   file path
     * @param config data map
     * @return flag
     */
    public static void writeXml(String path, Map<String, Object> config) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", config);

        writeTemplate(path, modelMap, "xml");
    }

}
