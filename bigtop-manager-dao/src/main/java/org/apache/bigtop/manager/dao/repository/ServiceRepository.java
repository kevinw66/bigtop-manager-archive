package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findAllByClusterId(Long clusterId);

    Page<Service> findAllByClusterId(Long clusterId, Pageable pageable);

    Service findByClusterIdAndServiceName(Long clusterId, String serviceName);

    List<Service> findByClusterIdAndServiceNameIn(Long clusterId, List<String> serviceNames);

}
