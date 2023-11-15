package org.apache.bigtop.manager.server.utils;

import org.apache.bigtop.manager.server.stack.pojo.PropertyModel;
import org.apache.bigtop.manager.server.stack.xml.ConfigurationXml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackConfigUtils {

    /**
     * load config from yaml file to Map<String,Object>
     *
     * @param fileName yaml config file name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadConfig(String fileName) {
        ConfigurationXml configurationXml = JaxbUtils.readFromPath(fileName, ConfigurationXml.class);
        return extractConfigMap(configurationXml.getPropertyModels());
    }

    /**
     * extract config from List<Map<String,Object>> to Map<String,Object>
     *
     * @param list List<Map<String, Object>>
     * @return
     */
    public static Map<String, Object> extractConfigMap(List<PropertyModel> list) {
        if (list == null) {
            return null;
        }

        Map<String, Object> hashMap = new HashMap<>();
        for (PropertyModel propertyModel : list) {
            String key = propertyModel.getName();
            Object value = propertyModel.getValue();
            hashMap.put(key, value);
        }
        return hashMap;
    }


}
