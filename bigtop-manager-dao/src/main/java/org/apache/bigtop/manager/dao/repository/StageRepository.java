package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByJob(Job job);
}
