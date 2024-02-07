package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    Optional<Cluster> findByClusterName(String clusterName);

}
