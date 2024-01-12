package org.apache.bigtop.manager.agent.scheduled;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.ComponentHeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ScriptInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.NetUtils;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.core.executor.Executor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 需要保证当前主机有组件安装
 */
@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class ComponentHeartbeatScheduled {

    @Resource
    private Executor executor;

    @Resource
    private AgentWsTools agentWsTools;

    @Async
    @Scheduled(cron = "0/30 * *  * * ?")
    public void execute() {
        Map<String, List<String>> hosts = LocalSettings.hosts();
        if (hosts.isEmpty()) {
            log.warn("All hosts are empty");
            return;
        }
        Map<String, ComponentInfo> components = LocalSettings.components();
        if (components.isEmpty()) {
            log.warn("All components are empty");
            return;
        }

        String hostname = NetUtils.getHostname();
        ClusterInfo clusterInfo = LocalSettings.cluster();

        for (Map.Entry<String, List<String>> entry : hosts.entrySet()) {
            String componentName = entry.getKey();
            List<String> hostnameList = entry.getValue();

            if (!componentName.equals(Constants.ALL_HOST_KEY) && hostnameList.contains(hostname)) {
                CommandPayload commandPayload = new CommandPayload();
                commandPayload.setCommand(Command.STATUS);
                commandPayload.setHostname(hostname);
                commandPayload.setStackName(clusterInfo.getStackName());
                commandPayload.setStackVersion(clusterInfo.getStackVersion());
                commandPayload.setRoot(clusterInfo.getRoot());
                commandPayload.setServiceName(components.get(componentName).getServiceName());
                try {
                    String commandScriptStr = components.get(componentName).getCommandScript();
                    ScriptInfo commandScript = JsonUtils.readFromString(commandScriptStr, ScriptInfo.class);
                    commandPayload.setCommandScript(commandScript);
                } catch (Exception e) {
                    log.error("{} commandScript is error", componentName, e);
                    break;
                }
                ShellResult shellResult = executor.execute(commandPayload);
                ComponentHeartbeatMessage resultMessage = new ComponentHeartbeatMessage();
                resultMessage.setCode(shellResult.getExitCode());
                resultMessage.setResult(shellResult.getResult());
                resultMessage.setHostname(commandPayload.getHostname());
                resultMessage.setComponentName(componentName);
                resultMessage.setServiceName(components.get(componentName).getServiceName());

                agentWsTools.sendMessage(resultMessage);
            }

        }

    }

}
