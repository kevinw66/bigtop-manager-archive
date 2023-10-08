package org.apache.bigtop.manager.common.message.type;

import lombok.*;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ScriptInfo;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CommandMessage extends BaseCommandMessage {

    @NonNull
    private String serviceName;

    @NonNull
    private Command command;

    private String customCommand;

    private Map<String, ScriptInfo> customCommands;

    @NonNull
    private ScriptInfo commandScript;

    private String serviceUser;

    private String serviceGroup;

    @NonNull
    private String stackName;

    @NonNull
    private String stackVersion;

    @NonNull
    private String root;

    private String componentName;

    private List<OSSpecificInfo> osSpecifics;

    @NonNull
    private String hostname;

}
