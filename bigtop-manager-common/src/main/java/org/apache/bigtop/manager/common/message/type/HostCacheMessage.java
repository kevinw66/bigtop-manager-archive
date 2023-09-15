package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.type.pojo.BasicInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class HostCacheMessage extends BaseMessage {

    private BasicInfo basicInfo;

    private ClusterInfo clusterInfo;

    private Map<String, Set<String>> userInfo;

    private List<RepoInfo> repoInfo;

    private Map<String, Map<String, Object>> configurations;

    private Map<String, Set<String>> clusterHostInfo;

    private String stackName;

    private String stackVersion;

    private String hostname;

}
