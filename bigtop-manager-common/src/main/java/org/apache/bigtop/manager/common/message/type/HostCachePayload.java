package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class HostCachePayload {

    private Map<String, Object> settings;

    private ClusterInfo clusterInfo;

    private Map<String, Set<String>> userInfo;

    private List<RepoInfo> repoInfo;

    private Map<String, Map<String, Object>> configurations;

    private Map<String, Set<String>> clusterHostInfo;

    private String hostname;

    private Map<String, ComponentInfo> componentInfo;

}
