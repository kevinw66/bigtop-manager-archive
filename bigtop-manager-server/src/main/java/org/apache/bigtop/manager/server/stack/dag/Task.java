package org.apache.bigtop.manager.server.stack.dag;

import lombok.*;
import org.apache.bigtop.manager.server.enums.RequestState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Task {

    private String taskId;

    private Long timeout;

    private RequestState state;

    @NonNull
    private String serviceName;

    @NonNull
    private String componentName;

    @NonNull
    private String command;

    @NonNull
    private String scriptId;

    @NonNull
    private String hostname;

    private Long stackId;

    private String stackName;

    private String stackVersion;

    private Long clusterId;

    private String clusterName;

    @NonNull
    private String root;

    private String serviceUser;

    private String serviceGroup;

    private String osSpecifics;
}
