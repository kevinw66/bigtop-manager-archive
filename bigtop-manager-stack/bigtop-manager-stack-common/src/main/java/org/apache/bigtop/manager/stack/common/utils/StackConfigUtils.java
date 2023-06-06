package org.apache.bigtop.manager.stack.common.utils;

import org.apache.bigtop.manager.common.utils.YamlUtils;

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
        List<Map<String, Object>> list = YamlUtils.readYaml(fileName, List.class);
        return extractConfigMap(list);
    }

    /**
     * extract config from List<Map<String,Object>> to Map<String,Object>
     *
     * @param list List<Map<String, Object>>
     * @return
     */
    public static Map<String, Object> extractConfigMap(List<Map<String, Object>> list) {
        if (list == null) {
            return null;
        }

        Map<String, Object> hashMap = new HashMap<>();
        for (Map<String, Object> map : list) {
            String key = String.valueOf(map.get("name"));
            Object value = map.get("value");
            hashMap.put(key, value);
        }
        return hashMap;
    }


}
