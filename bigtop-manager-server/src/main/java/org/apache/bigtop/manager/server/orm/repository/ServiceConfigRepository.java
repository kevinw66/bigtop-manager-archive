package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ServiceConfigRepository extends CrudRepository<ServiceConfig, Long> {

    List<ServiceConfig> findAllByClusterId(Long clusterId);

    @Query(value = "SELECT MAX(u.version) FROM ServiceConfig u WHERE u.cluster.id = ?1 AND u.service.id=?2 AND u.typeName = ?3")
    Optional<Integer> findMaxVersion(Long clusterId, Long serviceId, String typeName);
}
