package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoRepository extends JpaRepository<Repo, Long> {

    List<Repo> findAllByCluster(Cluster cluster);
}
