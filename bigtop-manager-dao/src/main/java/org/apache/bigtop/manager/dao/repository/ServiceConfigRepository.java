package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Service;
import org.apache.bigtop.manager.dao.entity.ServiceConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ServiceConfigRepository extends JpaRepository<ServiceConfig, Long> {

    List<ServiceConfig> findAllByCluster(Cluster cluster);

    List<ServiceConfig> findAllByCluster(Cluster cluster, Sort sort);

    List<ServiceConfig> findAllByClusterAndService(Cluster cluster, Service service);

    ServiceConfig findByClusterAndServiceAndSelectedIsTrue(Cluster cluster, Service service);

    List<ServiceConfig> findAllByClusterAndSelectedIsTrue(Cluster cluster);

    @Transactional
    @Modifying
    @Query("UPDATE ServiceConfig s SET s.selected = false WHERE s.cluster = :cluster AND s.service = :service")
    void setAllSelectedToFalseByClusterAndService(Cluster cluster, Service service);
}