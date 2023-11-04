package org.apache.bigtop.manager.server.scheduled;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.apache.bigtop.manager.server.enums.heartbeat.HostState;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class HostHeartbeatScheduled {

    @Resource
    private HostRepository hostRepository;

    @Async
    @Scheduled(cron = "0/30 * *  * * ? ")
    public void execute() {
        log.info("HostHeartbeatScheduled execute");

        List<Host> hosts = hostRepository.findAll();
        Map<String, Host> hostMap = hosts.stream().collect(Collectors.toMap(Host::getHostname, host -> host));
        for (Map.Entry<String, HeartbeatMessage> entry : ServerWebSocketHandler.HEARTBEAT_MESSAGE_MAP.entrySet()) {
            String hostname = entry.getKey();
            HeartbeatMessage heartbeatMessage = entry.getValue();
            HostInfo hostInfo = heartbeatMessage.getHostInfo();
            if (hostMap.containsKey(hostname)) {
                Host host = hostMap.get(hostname);
                if (hostInfo != null) {
                    host.setArch(hostInfo.getArch());
                    host.setAvailableProcessors(hostInfo.getAvailableProcessors());
                    host.setIpv4(hostInfo.getIpv4());
                    host.setIpv6(hostInfo.getIpv6());
                    host.setOs(hostInfo.getOs());
                    host.setTotalMemorySize(hostInfo.getTotalMemorySize());
                    host.setState(HostState.HEALTHY.name());
                }
                hostRepository.save(host);
                hostMap.remove(hostname);
            }
        }

        if (!hostMap.isEmpty()) {
            for (Map.Entry<String, Host> entry : hostMap.entrySet()) {
                Host host = entry.getValue();
                host.setState(HostState.LOST.name());
                hostRepository.save(host);
            }
        }

    }

}
