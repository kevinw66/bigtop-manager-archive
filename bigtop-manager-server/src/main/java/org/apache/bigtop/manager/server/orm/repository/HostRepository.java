package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Long> {

    Optional<Host> findByHostname(String hostname);

    List<Host> findAllByHostnameIn(Iterable<String> hostnames);

    List<Host> findAllByClusterId(Long clusterId);

    List<Host> findAllByClusterClusterName(String clusterName);

}
