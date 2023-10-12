package org.apache.bigtop.manager.stack.common.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;

import java.util.*;

import static org.apache.bigtop.manager.common.constants.HostCacheConstants.*;

@Slf4j
public class LocalSettings {

    public static Object configurations(String service, String type, String key, Object defaultValue) {
        Map<String, Object> configMap = configurations(service, type);
        return configMap.getOrDefault(key, defaultValue);
    }

    public static Map<String, Object> configurations(String service, String type) {
        String cacheDir = Constants.STACK_CACHE_DIR;

        Map<String, Object> configDataMap = new HashMap<>();
        try {
            Map<String, Map<String, Object>> configJson = JsonUtils.readFromFile(cacheDir + CONFIGURATIONS_INFO, new TypeReference<>() {
            });
            Object configData = configJson.getOrDefault(service, new HashMap<>()).get(type);
            if (configData != null) {
                configDataMap = JsonUtils.readFromString((String) configData, new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            log.warn("{} parse error, ", CONFIGURATIONS_INFO, e);
        }

        return configDataMap;
    }

    public static List<String> hosts(String service) {
        String cacheDir = Constants.STACK_CACHE_DIR;

        Map<String, List<String>> hostJson = new HashMap<>();
        try {
            hostJson = JsonUtils.readFromFile(cacheDir + HOSTS_INFO, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("{} parse error, ", HOSTS_INFO, e);
        }
        return hostJson.getOrDefault(service, List.of());
    }

    public static Map<String, Object> basicInfo() {
        String cacheDir = Constants.STACK_CACHE_DIR;

        Map<String, Object> settings = new HashMap<>();
        try {
            settings = JsonUtils.readFromFile(cacheDir + SETTINGS_INFO, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("{} parse error, ", SETTINGS_INFO, e);
        }
        return settings;
    }

    public static Map<String, Set<String>> users() {
        String cacheDir = Constants.STACK_CACHE_DIR;

        Map<String, Set<String>> userMap = new HashMap<>();
        try {
            userMap = JsonUtils.readFromFile(cacheDir + USERS_INFO, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("{} parse error, ", USERS_INFO, e);
        }
        return userMap;
    }

    public static Set<String> packages() {
        ClusterInfo cluster = cluster();
        return Optional.of(cluster.getPackages()).orElse(Set.of());
    }

    public static List<RepoInfo> repos() {
        String cacheDir = Constants.STACK_CACHE_DIR;

        List<RepoInfo> repoInfoList = List.of();
        try {
            repoInfoList = JsonUtils.readFromFile(cacheDir + REPOS_INFO, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("{} parse error, ", REPOS_INFO, e);
        }
        return repoInfoList;
    }

    public static ClusterInfo cluster() {
        String cacheDir = Constants.STACK_CACHE_DIR;

        TypeReference<ClusterInfo> typeReference = new TypeReference<>() {
        };
        ClusterInfo clusterInfo = new ClusterInfo();
        try {
            clusterInfo = JsonUtils.readFromFile(cacheDir + CLUSTER_INFO, typeReference);
        } catch (Exception e) {
            log.warn("{} parse error, ", CLUSTER_INFO, e);
        }
        return clusterInfo;
    }
}
