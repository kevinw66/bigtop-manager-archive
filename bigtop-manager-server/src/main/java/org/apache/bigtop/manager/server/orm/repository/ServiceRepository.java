package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Service;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends CrudRepository<Service, Long> {

    List<Service> findAllByClusterId(Long clusterId);

    Optional<Service> findByServiceName(String serviceName);
}
