package org.apache.bigtop.manager.agent.utils.template;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class XmlTemplate extends BaseTemplate {

    /**
     * Write xml file
     *
     * @param path   file path
     * @param config data map
     * @return flag
     */
    public void writeXml(String path, Map<String, Object> config) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", config);

        super.writeTemplate(path, modelMap, "xml");
    }

}
