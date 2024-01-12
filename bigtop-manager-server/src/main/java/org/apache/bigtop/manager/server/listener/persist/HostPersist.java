package org.apache.bigtop.manager.server.listener.persist;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HostPersist {

    @Resource
    private HostRepository hostRepository;

    public void persist(Cluster cluster, List<String> hostnames) {
        List<Host> hostnameIn = hostRepository.findAllByHostnameIn(hostnames);
        List<Host> hosts = new ArrayList<>();

        Map<String, Host> hostInMap = hostnameIn.stream().collect(Collectors.toMap(Host::getHostname, host -> host));

        for (String hostname : hostnames) {
            Host host = new Host();
            host.setHostname(hostname);
            host.setCluster(cluster);
            host.setState(MaintainState.INSTALLED);

            if (hostInMap.containsKey(hostname)) {
                host.setId(hostInMap.get(hostname).getId());
            }

            hosts.add(host);
        }
        hostRepository.saveAll(hosts);
    }
}
