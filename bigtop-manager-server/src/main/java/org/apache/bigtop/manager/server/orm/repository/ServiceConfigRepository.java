package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ServiceConfigRepository extends CrudRepository<ServiceConfig, Long> {

    List<ServiceConfig> findAllByClusterId(Long clusterId);

    Optional<ServiceConfig> findFirstByClusterIdAndServiceIdAndTypeNameOrderByVersionDesc(Long clusterId, Long serviceId, String typeName);

    Optional<ServiceConfig> findFirstByClusterIdAndServiceIdAndTypeNameAndVersion(Long clusterId, Long serviceId, String typeName, Integer version);
}
