package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findAllByClusterId(Long clusterId);

    List<Service> findAllByClusterClusterName(String clusterName);

    Optional<Service> findByServiceName(String serviceName);

    Optional<Service> findByClusterClusterNameAndServiceName(String clusterName, String serviceName);

    List<Service> findAllByClusterClusterNameAndServiceNameIn(String clusterName, List<String> serviceName);
}
