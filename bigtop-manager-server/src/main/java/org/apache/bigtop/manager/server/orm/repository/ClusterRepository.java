package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClusterRepository extends CrudRepository<Cluster, Long> {
    Optional<Cluster> findByClusterName(String clusterName);
}
