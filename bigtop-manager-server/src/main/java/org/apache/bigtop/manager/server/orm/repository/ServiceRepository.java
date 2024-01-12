package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findAllByClusterId(Long clusterId);

    Page<Service> findAllByClusterId(Long clusterId, Pageable pageable);

    Optional<Service> findByClusterIdAndServiceName(Long clusterId, String serviceName);

    List<Service> findByClusterIdAndServiceNameIn(Long clusterId, List<String> serviceNames);

}
