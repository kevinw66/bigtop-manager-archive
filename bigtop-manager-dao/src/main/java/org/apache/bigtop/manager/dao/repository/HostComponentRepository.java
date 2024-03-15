package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.HostComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostComponentRepository extends JpaRepository<HostComponent, Long> {

    List<HostComponent> findAllByComponentClusterIdAndComponentComponentName(Long clusterId, String componentName);

    HostComponent findByComponentClusterIdAndComponentComponentNameAndHostHostname(Long clusterId, String componentName, String hostnames);

    List<HostComponent> findAllByComponentClusterIdAndComponentComponentNameAndHostHostnameIn(Long clusterId, String componentName, List<String> hostnames);

    List<HostComponent> findAllByComponentClusterId(Long clusterId);

    HostComponent findByComponentComponentNameAndHostHostname(String componentName, String hostName);

    List<HostComponent> findAllByComponentClusterIdAndHostId(Long clusterId, Long componentId);

    List<HostComponent> findAllByComponentClusterIdAndComponentServiceId(Long clusterId, Long serviceId);

    List<HostComponent> findAllByComponentServiceId(Long serviceId);
}
