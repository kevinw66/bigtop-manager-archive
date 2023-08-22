package org.apache.bigtop.manager.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;


    @Override
    public void handleComponent(String clusterName, String componentName, String action) {

        Component component = componentRepository.findByClusterNameAndComponentName(clusterName, componentName).orElse(new Component());

        Cluster cluster = component.getCluster();

        if (StringUtils.isBlank(cluster.getStack().getStackName())
                || StringUtils.isBlank(cluster.getStack().getStackVersion())
                || StringUtils.isBlank(component.getScriptId())) {
            return;
        }

        CommandMessage commandMessage = new CommandMessage();

        commandMessage.setStack(cluster.getStack().getStackName());
        commandMessage.setVersion(cluster.getStack().getStackVersion());
        commandMessage.setScriptId(component.getScriptId());
        commandMessage.setService(component.getService().getServiceName());
        commandMessage.setComponent(componentName);
        commandMessage.setRoot(cluster.getRoot());

        commandMessage.setCacheDir(component.getCluster().getCacheDir());

        try {
            List<OSSpecificInfo> osSpecifics = JsonUtils.OBJECTMAPPER.readValue(component.getService().getOsSpecifics(),
                    new TypeReference<>() {
                    });
            commandMessage.setOsSpecifics(osSpecifics);
        } catch (Exception e) {
            return;
        }
        commandMessage.setCommand(action);
        commandMessage.setServiceUser(component.getService().getServiceUser());
        commandMessage.setServiceGroup(component.getService().getServiceGroup());

        List<HostComponent> hostComponents = hostComponentRepository.findByComponent(component);


        for (HostComponent hostComponent : hostComponents) {
            String hostname = hostComponent.getHost().getHostname();
            //TODO The messageId needs to be redefined
            String messageId = StringUtils.joinWith("_", hostname, clusterName, componentName, UUID.randomUUID().toString());
            commandMessage.setMessageId(messageId);
            commandMessage.setHostname(hostname);
            commandMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));

            log.info("commandMessage: {}", commandMessage);
            serverWebSocketHandler.sendMessage(hostname, commandMessage);
        }
    }
}
