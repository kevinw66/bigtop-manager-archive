package org.apache.bigtop.manager.common.message.type;

import lombok.*;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CommandMessage extends BaseMessage {

    @NonNull
    private String serviceName;

    @NonNull
    private String command;

    private String serviceUser;

    private String serviceGroup;

    @NonNull
    private String stackName;

    @NonNull
    private String stackVersion;

    @NonNull
    private String root;

    private String componentName;

    @NonNull
    private String scriptId;

    private List<OSSpecificInfo> osSpecifics;

    @NonNull
    private String hostname;

}
