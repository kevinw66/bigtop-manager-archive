package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {

    Component findByClusterIdAndComponentName(Long clusterId, String componentName);

    List<Component> findAllByClusterIdAndComponentNameIn(Long clusterId, List<String> componentNames);

    List<Component> findAllByClusterId(Long clusterId);

    List<Component> findAllByClusterIdAndServiceServiceNameIn(Long clusterId, List<String> serviceNames);
}
