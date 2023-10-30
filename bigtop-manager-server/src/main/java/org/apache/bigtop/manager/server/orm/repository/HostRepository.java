package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface HostRepository extends JpaRepository<Host, Long> {

    Host findByHostname(String hostname);

    List<Host> findAllByHostnameIn(Collection<String> hostnames);

    List<Host> findAllByClusterId(Long clusterId);

    List<Host> findAllByClusterClusterName(String clusterName);

}
