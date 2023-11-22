package org.apache.bigtop.manager.server.utils;

import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.stack.pojo.PropertyModel;
import org.apache.bigtop.manager.server.stack.xml.ConfigurationXml;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackConfigUtils {

    /**
     * load config from yaml file to Map<String,Object>
     *
     * @param fileName yaml config file name
     * @return Map<String, Object>
     */
    public static ImmutableTriple<Map<String, Object>, Map<String, String>, Map<String, PropertyDTO>> loadConfig(String fileName) {
        ConfigurationXml configurationXml = JaxbUtils.readFromPath(fileName, ConfigurationXml.class);
        List<PropertyModel> propertyModels = configurationXml.getPropertyModels();

        Map<String, PropertyDTO> propertyDTOMap = new HashMap<>();
        for (PropertyModel propertyModel : propertyModels) {
            PropertyDTO propertyDTO = new PropertyDTO();
            propertyDTO.setDisplayName(propertyModel.getDisplayName());
            propertyDTO.setDesc(propertyModel.getDesc());
            propertyDTOMap.put(propertyModel.getName(), propertyDTO);
        }

        Map<String, String> attributes = parseAttributes(configurationXml);
        Map<String, Object> configData = extractConfigMap(propertyModels);
        return new ImmutableTriple<>(configData, attributes, propertyDTOMap);
    }

    /**
     * extract config from List<Map<String,Object>> to Map<String,Object>
     *
     * @param list List<PropertyModel>
     * @return Map<String, Object>
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

    /**
     * Parse a configurations type attributes.
     *
     * @param configuration  object representation of a configuration file
     *
     * @return collection of attributes for the configuration type
     */
    private static Map<String, String> parseAttributes(ConfigurationXml configuration) {
        Map<String, String> attributes = new HashMap<>();
        for (Map.Entry<QName, String> attribute : configuration.getAttributes().entrySet()) {
            attributes.put(attribute.getKey().getLocalPart(), attribute.getValue());
        }
        return attributes;
    }

}
