package org.apache.bigtop.manager.server.utils;

import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.stack.pojo.PropertyModel;
import org.apache.bigtop.manager.server.stack.xml.ConfigurationXml;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.xml.namespace.QName;
import java.util.ArrayList;
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
    public static ImmutablePair<List<PropertyDTO>, Map<String, String>> loadConfig(String fileName) {
        ConfigurationXml configurationXml = JaxbUtils.readFromPath(fileName, ConfigurationXml.class);
        List<PropertyModel> propertyModels = configurationXml.getPropertyModels();

        List<PropertyDTO> propertyDTOList = new ArrayList<>();
        for (PropertyModel propertyModel : propertyModels) {
            PropertyDTO propertyDTO = new PropertyDTO();
            propertyDTO.setDisplayName(propertyModel.getDisplayName());
            propertyDTO.setDesc(propertyModel.getDesc());
            propertyDTO.setName(propertyModel.getName());
            propertyDTO.setValue(propertyModel.getValue());
            propertyDTOList.add(propertyDTO);
        }

        Map<String, String> attributes = parseAttributes(configurationXml);
        return new ImmutablePair<>(propertyDTOList, attributes);
    }

    /**
     * extract config from List<Map<String,Object>> to Map<String,Object>
     *
     * @param list List<PropertyModel>
     * @return Map<String, Object>
     */
    public static Map<String, Object> extractConfigMap(List<PropertyDTO> list) {
        if (list == null) {
            return null;
        }

        Map<String, Object> hashMap = new HashMap<>();
        for (PropertyDTO property : list) {
            String key = property.getName();
            Object value = property.getValue();
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
