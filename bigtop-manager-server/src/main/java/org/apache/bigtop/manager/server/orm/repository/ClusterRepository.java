package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    Optional<Cluster> findByClusterName(String clusterName);

    Cluster findByClusterNameAndState(String clusterName, MaintainState state);
}
