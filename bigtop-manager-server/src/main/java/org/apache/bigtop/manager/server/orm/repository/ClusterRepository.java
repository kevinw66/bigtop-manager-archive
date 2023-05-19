package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.springframework.data.repository.CrudRepository;

public interface ClusterRepository extends CrudRepository<Cluster, Long> {
}
