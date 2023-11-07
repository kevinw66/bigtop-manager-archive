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
    @Scheduled(cron = "0/30 * *  * * ? ")
    public void execute() {
        log.info("ComponentHeartbeatScheduled execute");

        Map<String, List<String>> hosts = LocalSettings.hosts();
        if (hosts.isEmpty()) {
            log.warn("hosts is empty");
            return;
        }
        Map<String, ComponentInfo> components = LocalSettings.components();
        if (components.isEmpty()) {
            log.warn("components is empty");
            return;
        }

        String hostname = NetUtils.getHostname();
        ClusterInfo clusterInfo = LocalSettings.cluster();

        if (log.isDebugEnabled()) {
            log.debug("hosts:{}", hosts);
            log.debug("components:{}", components);
            log.debug("clusterInfo:{}", clusterInfo);
        }
        for (Map.Entry<String, List<String>> entry : hosts.entrySet()) {
            String componentName = entry.getKey();
            List<String> hostnameList = entry.getValue();

            if (!componentName.equals(Constants.ALL_HOST_KEY) && hostnameList.contains(hostname)) {
                CommandPayload commandMessage = new CommandPayload();
                commandMessage.setCommand(Command.STATUS);
                commandMessage.setHostname(hostname);
                commandMessage.setStackName(clusterInfo.getStackName());
                commandMessage.setStackVersion(clusterInfo.getStackVersion());
                commandMessage.setRoot(clusterInfo.getRoot());
                commandMessage.setServiceName(components.get(componentName).getServiceName());
                try {
                    String commandScriptStr = components.get(componentName).getCommandScript();
                    ScriptInfo commandScript = JsonUtils.readFromString(commandScriptStr, ScriptInfo.class);
                    commandMessage.setCommandScript(commandScript);
                } catch (Exception e) {
                    log.error("{} commandScript is error", componentName, e);
                    break;
                }
                log.info("ComponentHeartbeatScheduled-commandMessage:{}", commandMessage);
                Object result = executor.execute(commandMessage);
                log.info("ComponentHeartbeatScheduled-result:{}", result);
                if (result instanceof ShellResult shellResult) {
                    ComponentHeartbeatMessage resultMessage = new ComponentHeartbeatMessage();
                    resultMessage.setCode(shellResult.getExitCode());
                    resultMessage.setResult(shellResult.getResult());
                    resultMessage.setHostname(commandMessage.getHostname());

                    agentWsTools.sendMessage(resultMessage);
                }
            }

        }

    }

}
