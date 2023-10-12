package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    Optional<Component> findByClusterClusterNameAndComponentName(String clusterName, String componentName);

    List<Component> findAllByClusterClusterNameAndComponentNameIn(String clusterName, Iterable<String> componentNames);

    List<Component> findAllByClusterClusterName(String clusterName);

    List<Component> findAllByClusterClusterNameAndServiceServiceNameIn(String clusterName, Iterable<String> serviceNames);
}
