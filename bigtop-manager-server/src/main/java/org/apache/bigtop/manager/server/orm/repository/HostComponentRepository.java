package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HostComponentRepository extends JpaRepository<HostComponent, Long> {

    List<HostComponent> findAllByComponentClusterClusterNameAndComponentComponentName(String clusterName, String componentName);

    List<HostComponent> findAllByComponentClusterId(Long clusterId);

    Optional<HostComponent> findByComponentComponentNameAndHostHostname(String componentName, String hostName);

    List<HostComponent> findAllByComponentClusterIdAndHostId(Long clusterId, Long componentId);

    List<HostComponent> findAllByComponentClusterIdAndComponentServiceId(Long clusterId, Long serviceId);

    List<HostComponent> findAllByComponentServiceId(Long serviceId);
}
