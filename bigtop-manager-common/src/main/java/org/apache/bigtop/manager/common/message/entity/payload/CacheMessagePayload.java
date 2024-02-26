package org.apache.bigtop.manager.common.message.entity.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.entity.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.entity.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.entity.pojo.RepoInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CacheMessagePayload extends BasePayload {

    private Map<String, Object> settings;

    private ClusterInfo clusterInfo;

    private Map<String, Set<String>> userInfo;

    private List<RepoInfo> repoInfo;

    private Map<String, Map<String, Object>> configurations;

    private Map<String, Set<String>> clusterHostInfo;

    private Map<String, ComponentInfo> componentInfo;

}
