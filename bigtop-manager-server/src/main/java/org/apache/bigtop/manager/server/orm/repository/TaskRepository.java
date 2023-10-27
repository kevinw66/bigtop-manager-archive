package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStage(Stage stage);

    List<Task> findAllByJobId(Long jobId);

    List<Task> findAllByJobIdAndState(Long jobId, JobState state);

    List<Task> findAllByJobIdAndHostnameAndState(Long jobId, String hostname, JobState state);

}
