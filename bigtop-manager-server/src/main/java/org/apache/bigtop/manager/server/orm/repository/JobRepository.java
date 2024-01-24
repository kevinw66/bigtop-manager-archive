package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findAllByClusterIsNull(Pageable pageable);

    Page<Job> findAllByClusterId(Long clusterId, Pageable pageable);

    List<Job> findAllByClusterId(Long clusterId);
}
