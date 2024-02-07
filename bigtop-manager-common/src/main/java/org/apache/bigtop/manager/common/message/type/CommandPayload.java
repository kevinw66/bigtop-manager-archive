package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.pojo.CustomCommandInfo;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ScriptInfo;

import java.util.List;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class CommandPayload {

    private String serviceName;

    private Command command;

    private String customCommand;

    private List<CustomCommandInfo> customCommands;

    private ScriptInfo commandScript;

    private String serviceUser;

    private String serviceGroup;

    private String stackName;

    private String stackVersion;

    private String root;

    private String componentName;

    private List<OSSpecificInfo> osSpecifics;

    private String hostname;

}
