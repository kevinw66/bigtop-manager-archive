package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    Optional<Component> findByClusterIdAndComponentName(Long clusterId, String componentName);

    List<Component> findAllByClusterIdAndComponentNameIn(Long clusterId, List<String> componentNames);

    List<Component> findAllByClusterId(Long clusterId);

    List<Component> findAllByClusterIdAndServiceServiceNameIn(Long clusterId, List<String> serviceNames);
}
