package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Component;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ComponentRepository extends CrudRepository<Component, Long> {

    @Query(value = "select u from Component u where u.cluster.clusterName=?1 and u.componentName=?2")
    Optional<Component> findByClusterNameAndComponentName(String clusterName, String componentName);
}
