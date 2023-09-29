package org.apache.bigtop.manager.server.service.impl;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.StatusType;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;
import org.apache.bigtop.manager.server.enums.heartbeat.HostState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.event.HostCheckEvent;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.ws.ServerWebSocketSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HostServiceImpl implements HostService {

    @Resource
    private HostRepository hostRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private EventBus eventBus;

    @Override
    public List<HostVO> list() {
        List<HostVO> hostVOList = new ArrayList<>();
        hostRepository.findAll().forEach(host -> {
            HostVO hostVO = HostMapper.INSTANCE.Entity2VO(host);
            hostVOList.add(hostVO);
        });

        return hostVOList;
    }

    @Override
    public List<HostVO> create(HostDTO hostDTO) {
        List<String> hostnameList = hostDTO.getHostnames();

        Cluster cluster = clusterRepository.findById(hostDTO.getClusterId()).orElse(new Cluster());

        List<Host> hostList = new ArrayList<>();
        for (String hostname : hostnameList) {
            //websocket
            int retry = 3;
            Host host = new Host();
            while (retry >= 0) {
                WebSocketSession webSocketSession = ServerWebSocketSessionManager.SESSIONS.get(hostname);
                boolean open = webSocketSession.isOpen();
                System.out.println("open = " + open);

                if (open) {
                    HeartbeatMessage heartbeatMessage = ServerWebSocketSessionManager.HEARTBEAT_MESSAGE_MAP.get(hostname);
                    host = HostMapper.INSTANCE.Message2Entity(heartbeatMessage.getHostInfo());
                    Host savedHost = hostRepository.findByHostname(hostname).orElse(new Host());
                    if (savedHost.getId() != null) {
                        host.setId(savedHost.getId());
                    }
                    // todo 向前端发送进度消息

                    break;
                }
                retry--;
                try {
                    Thread.sleep(Constants.REGISTRY_SESSION_TIMEOUT);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //persist host to database
            host.setCluster(cluster);
            host.setStatus(StatusType.INSTALLED.getCode());
            host.setState(HostState.INSTALLED.name());
            hostList.add(host);
        }
        hostList = Lists.newArrayList(hostRepository.saveAll(hostList));

        // time server detection
        HostCheckEvent hostCheckEvent = HostMapper.INSTANCE.DTO2CheckEvent(hostDTO);
        eventBus.post(hostCheckEvent);

        // cache
        cache(cluster.getId());

        return HostMapper.INSTANCE.Entity2VO(hostList);
    }

    @Override
    public HostVO get(Long id) {
        Host host = hostRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.HOST_NOT_FOUND));

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public HostVO update(Long id, HostDTO hostDTO) {
        Host host = HostMapper.INSTANCE.DTO2Entity(hostDTO);
        host.setId(id);
        hostRepository.save(host);

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public Boolean delete(Long id) {
        hostRepository.deleteById(id);
        return true;
    }

    @Override
    public List<HostComponentVO> hostComponent(Long id) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByHostId(id);
        return HostComponentMapper.INSTANCE.Entity2VO(hostComponentList);
    }

    @Override
    public Boolean cache(Long clusterId) {
        eventBus.post(new HostCacheEvent(clusterId));
        return true;
    }

    @Override
    public CommandVO command(CommandDTO commandDTO) {
        Command command = commandDTO.getCommand();
        List<String> componentNameList = commandDTO.getComponentNames();
        String hostname = commandDTO.getHostname();
        String clusterName = commandDTO.getClusterName();
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());

        //persist request to database
        Job job = JobMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        job = jobRepository.save(job);

        if (command == Command.INSTALL) {
            //Persist hostComponent to database
            List<Component> componentList = componentRepository.findAllByClusterClusterNameAndComponentNameIn(clusterName, componentNameList);
            Host host = hostRepository.findByHostname(hostname).orElse(new Host());
            for (Component component : componentList) {
                HostComponent hostComponent = new HostComponent();
                hostComponent.setHost(host);
                hostComponent.setComponent(component);
                hostComponent.setStatus(StatusType.UNINSTALLED.getCode());
                hostComponent.setState(CommandState.UNINSTALLED);

                Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(component.getComponentName(), host.getHostname());
                hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                hostComponentRepository.save(hostComponent);
            }
            // cache
            cache(cluster.getId());
        }

        CommandEvent commandEvent = CommandMapper.INSTANCE.DTO2Event(commandDTO, job);
        eventBus.post(commandEvent);

        return JobMapper.INSTANCE.Entity2VO(job);
    }

}
