package org.apache.bigtop.manager.server.service.impl;

import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.server.enums.CommandEvent;
import org.apache.bigtop.manager.server.enums.HostState;
import org.apache.bigtop.manager.server.enums.RequestState;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.mapper.RequestMapper;
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
    private RequestRepository requestRepository;

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
    public HostVO create(HostDTO hostDTO) {
        String hostname = hostDTO.getHostname();

        Cluster cluster = clusterRepository.findById(hostDTO.getClusterId()).orElse(new Cluster());

        //websocket
        int retry = 3;
        Host host = new Host();
        while (retry >= 0) {
            WebSocketSession webSocketSession = ServerWebSocketSessionManager.SESSIONS.get(hostDTO.getHostname());
            boolean open = webSocketSession.isOpen();
            System.out.println("open = " + open);

            if (open) {
                HeartbeatMessage heartbeatMessage = ServerWebSocketSessionManager.HEARTBEAT_MESSAGE_MAP.get(hostname);
                host = HostMapper.INSTANCE.Message2Entity(heartbeatMessage.getHostInfo());
                Host savedHost = hostRepository.findByHostname(hostDTO.getHostname()).orElse(new Host());
                if (savedHost.getId() != null) {
                    host.setId(savedHost.getId());
                }
                // todo 向前端发送进度消息

                break;
            }
            retry--;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        //检测时间同步服务
        eventBus.post(hostname);

        host.setCluster(cluster);
        host.setStatus(true);
        host.setState(HostState.REGISTERED.name());
        host = hostRepository.save(host);

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public HostVO get(Long id) {
        Host host = hostRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.HOST_NOT_FOUND));

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
    public Boolean cache(Long clusterId) {
        eventBus.post(clusterId);
        return true;
    }

    @Override
    public CommandVO command(CommandDTO commandDTO) {
        String command = commandDTO.getCommand();
        List<String> componentNameList = commandDTO.getComponentNames();
        String hostname = commandDTO.getHostname();
        String clusterName = commandDTO.getClusterName();

        if (command.equals(CommandEvent.INSTALL.name())) {
            //Persist hostComponent to database
            List<Component> componentList = componentRepository.findAllByClusterClusterNameAndComponentNameIn(clusterName, componentNameList);
            Host host = hostRepository.findByHostname(hostname).orElse(new Host());
            for (Component component : componentList) {
                HostComponent hostComponent = new HostComponent();
                hostComponent.setHost(host);
                hostComponent.setComponent(component);
                hostComponent.setStatus(true);

                Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(component.getComponentName(), host.getHostname());
                hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                hostComponentRepository.save(hostComponent);
            }
        }

        eventBus.post(commandDTO);

        //persist request to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        Request request = RequestMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        request.setState(RequestState.PENDING.name());
        request = requestRepository.save(request);

        return RequestMapper.INSTANCE.Entity2VO(request);
    }

}
