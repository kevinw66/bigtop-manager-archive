package org.apache.bigtop.manager.stack.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static org.apache.bigtop.manager.common.constants.HostCacheConstants.CONFIGURATIONS_INFO;

public abstract class AbstractParams {

    public static CommandMessage commandMessage;

    /**
     * get the package list according to the os and arch
     */
    public static List<String> getPackageList() {
        List<OSSpecificInfo> osSpecifics = commandMessage.getOsSpecifics();
        if (osSpecifics == null) {
            return null;
        }

        String os = OSDetection.getOS();
        String arch = OSDetection.getArch();
        for (OSSpecificInfo osSpecific : osSpecifics) {
            List<String> pkgOS = osSpecific.getOs();
            List<String> pkgArch = osSpecific.getArch();
            if (pkgOS.contains(os) && pkgArch.contains(arch)) {
                return osSpecific.getPackages();
            }
        }

        return null;
    }

    /**
     * service home dir
     */
    public static String serviceHome() {
        String version = commandMessage.getVersion();
        String service = commandMessage.getService();
        String root = commandMessage.getRoot();

        return root + "/" + version + "/usr/lib/" + service.toLowerCase();
    }

    /**
     * service conf dir
     */
    public static String confDir() {
        return "/etc/" + commandMessage.getService() + "/conf";
    }

    public static String user() {
        return StringUtils.isNotBlank(commandMessage.getServiceUser()) ? commandMessage.getServiceUser() : "root";
    }

    public static String group() {
        return StringUtils.isNotBlank(commandMessage.getServiceGroup()) ? commandMessage.getServiceGroup() : "root";
    }

    public static String serviceName() {
        return commandMessage.getService();
    }

    /**
     * Get all service configurations
     */
    public static Map<String, Map<String, Object>> configDict() {
        String cacheDir = commandMessage.getCacheDir();

        TypeReference<Map<String, Map<String, Object>>> typeReference = new TypeReference<>() {
        };

        return JsonUtils.readJson(cacheDir + CONFIGURATIONS_INFO, typeReference);
    }

    /**
     * Get the configuration of a service
     * @param serviceName service name
     * @param typeName property type name
     * @return property value
     */
    public static Map<String, Object> configDict(String serviceName, String typeName) {
        Map<String, Map<String, Object>> config = configDict();

        Object configData = config.get(serviceName).get(typeName);

        try {
            return JsonUtils.OBJECTMAPPER.readValue(configData.toString(),
                    new TypeReference<>() {
                    });
        } catch (JsonProcessingException e) {
            throw new StackException(e);
        }
    }

}
