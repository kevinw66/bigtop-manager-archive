package org.apache.bigtop.manager.stack.common.utils.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TemplateUtils {

    /**
     * writeProperties to file
     *
     * @param fileName  fileName
     * @param configMap configMap
     * @param paramMap paramMap parameters for template
     */
    public static void map2Template(ConfigType configType, String fileName, Object configMap, Object paramMap) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", configMap);
        try {
            if (paramMap == null) {
                BaseTemplate.writeTemplate(fileName, modelMap, configType.name());
            } else {
                String paramTemplate = BaseTemplate.writeTemplateAsString(modelMap, configType.name());
                BaseTemplate.writeCustomTemplate(fileName, paramMap, paramTemplate);
            }
        } catch (Exception e) {
            log.error("writeProperties error,", e);
        }
    }

    /**
     * write custom template
     */
    public static void map2CustomTemplate(String template, String fileName, Object configMap, Object paramMap) {
        try {
            if (paramMap == null) {
                BaseTemplate.writeCustomTemplate(fileName, configMap, template);
            } else {
                String paramTemplate = BaseTemplate.writeCustomTemplateAsString(configMap, template);
                BaseTemplate.writeCustomTemplate(fileName, paramMap, paramTemplate);
            }
        } catch (Exception e) {
            log.error("writeProperties error,", e);
        }
    }

}
