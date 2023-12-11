package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.server.enums.MaintainState;

@Data
public class ServiceVO {

    private Long id;

    private String serviceName;

    private String displayName;

    private String serviceDesc;

    private String serviceVersion;

    private String clusterName;

    private String serviceUser;

    private String serviceGroup;

    private MaintainState state;
}
