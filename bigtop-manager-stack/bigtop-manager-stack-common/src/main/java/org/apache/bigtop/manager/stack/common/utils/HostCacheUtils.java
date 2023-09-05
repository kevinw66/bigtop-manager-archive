package org.apache.bigtop.manager.stack.common.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.bigtop.manager.common.message.type.pojo.BasicInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.stack.common.AbstractParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.bigtop.manager.common.constants.HostCacheConstants.*;

public class HostCacheUtils {

    public static Object configurations(String service, String type, String key, Object defaultValue) {
        Map<String, Object> configMap = configurations(service, type);

        return configMap == null ? defaultValue : configMap.getOrDefault(key, defaultValue);
    }

    public static Map<String, Object> configurations(String service, String type) {
        String cacheDir = AbstractParams.commandMessage.getCacheDir();

        TypeReference<Map<String, Map<String, Map<String, Object>>>> typeReference = new TypeReference<>() {
        };
        Map<String, Map<String, Map<String, Object>>> configJson = JsonUtils.readJson(cacheDir + CONFIGURATIONS_INFO, typeReference);

        return configJson.getOrDefault(service, new HashMap<>()).get(type);
    }

    public static Set<String> hosts(String service) {
        String cacheDir = AbstractParams.commandMessage.getCacheDir();

        TypeReference<Map<String, Set<String>>> typeReference = new TypeReference<>() {
        };
        Map<String, Set<String>> hostJson = JsonUtils.readJson(cacheDir + HOSTS_INFO, typeReference);

        return hostJson.get(service) == null ? Set.of() : hostJson.get(service);
    }

    public static BasicInfo basicInfo() {
        String cacheDir = AbstractParams.commandMessage.getCacheDir();

        TypeReference<BasicInfo> typeReference = new TypeReference<>() {
        };

        BasicInfo basicInfo = JsonUtils.readJson(cacheDir + BASIC_INFO, typeReference);

        return basicInfo == null ? new BasicInfo() : basicInfo;
    }

    public static Map<String, Set<String>> users() {
        String cacheDir = AbstractParams.commandMessage.getCacheDir();

        TypeReference<Map<String, Set<String>>> typeReference = new TypeReference<>() {
        };

        Map<String, Set<String>> userMap = JsonUtils.readJson(cacheDir + USERS_INFO, typeReference);

        return userMap == null ? new HashMap<>() : userMap;
    }

    public static Set<String> packages() {
        ClusterInfo cluster = cluster();
        return cluster.getPackages();
    }

    public static List<RepoInfo> repos() {
        String cacheDir = AbstractParams.commandMessage.getCacheDir();

        TypeReference<List<RepoInfo>> typeReference = new TypeReference<>() {
        };
        List<RepoInfo> repoInfoList = JsonUtils.readJson(cacheDir + REPOS_INFO, typeReference);

        return repoInfoList == null ? List.of() : repoInfoList;
    }

    public static ClusterInfo cluster() {
        String cacheDir = AbstractParams.commandMessage.getCacheDir();

        TypeReference<ClusterInfo> typeReference = new TypeReference<>() {
        };
        ClusterInfo clusterInfo = JsonUtils.readJson(cacheDir + CLUSTER_INFO, typeReference);
        return clusterInfo == null ? new ClusterInfo() : clusterInfo;
    }
}
