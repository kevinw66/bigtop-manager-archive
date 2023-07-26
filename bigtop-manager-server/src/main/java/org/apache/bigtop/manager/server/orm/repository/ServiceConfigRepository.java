package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ServiceConfigRepository extends CrudRepository<ServiceConfig, Long> {

    List<ServiceConfig> findAllByClusterId(Long clusterId);
}
