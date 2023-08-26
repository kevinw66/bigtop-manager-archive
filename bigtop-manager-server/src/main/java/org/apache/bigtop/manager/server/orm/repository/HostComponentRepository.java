package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface HostComponentRepository extends CrudRepository<HostComponent, Long> {

    List<HostComponent> findAllByComponentClusterClusterNameAndComponentComponentName(String clusterName, String componentName);

    List<HostComponent> findAllByComponentClusterId(Long clusterId);

    Optional<HostComponent> findByComponentComponentNameAndHostHostname(String componentName, String hostName);

}
