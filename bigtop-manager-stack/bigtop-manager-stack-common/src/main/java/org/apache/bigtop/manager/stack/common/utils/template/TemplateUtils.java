package org.apache.bigtop.manager.stack.common.utils.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.stack.StackConfigUtils;
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
     */
    public static void map2Template(String fileName, Map<String, Object> configMap, ConfigType configType) {
        HashMap<Object, Object> modelMap = new HashMap<>();
        modelMap.put("model", configMap);
        try {
            BaseTemplate.writeTemplate(fileName, modelMap, configType.name());
        } catch (Exception e) {
            log.error("writeProperties error,", e);
        }
    }

    /**
     * writeProperties to file
     *
     * @param fileName  fileName
     * @param configMap configMap
     * @param paramMap paramMap parameters for template
     */
    public static void map2TemplateByParam(String fileName, Map<String, Object> configMap, Map<String, Object> paramMap, ConfigType configType) {
        HashMap<Object, Object> modelMap = new HashMap<>();
        modelMap.put("model", configMap);
        try {
            String properties = BaseTemplate.writeTemplateAsString(modelMap, configType.name());
            BaseTemplate.writeCustomTemplate(fileName, paramMap, properties);
        } catch (Exception e) {
            log.error("writeProperties error,", e);
        }
    }

    public static void main(String[] args) {
        String fileName = "/opt/code/bigtop-manager/logs/test.properties";
        Map<String, Object> configMap = StackConfigUtils.loadConfig("/opt/code/bigtop-manager/bigtop-manager-server/src/main/resources/stacks/BIGTOP/3.3.0/services/KAFKA/configuration/kafka-broker.yaml");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("dir", "/a");
        map2TemplateByParam(fileName, configMap, paramMap, ConfigType.PROPERTIES);
    }
}
