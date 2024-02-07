package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.JobState;
import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.entity.Task;
import org.apache.bigtop.manager.dao.repository.ClusterRepository;
import org.apache.bigtop.manager.dao.repository.JobRepository;
import org.apache.bigtop.manager.dao.repository.StageRepository;
import org.apache.bigtop.manager.dao.repository.TaskRepository;

import java.util.List;

public abstract class AbstractJobFactory implements JobFactory {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    protected JobContext jobContext;

    protected Cluster cluster;

    protected Job job;

    @Override
    public Job createJob(JobContext jobContext) {
        this.jobContext = jobContext;

        // Create and init job
        initJob();

        // Create stages and tasks for job
        job.setStages(createStagesAndTasks());

        // Save job
        saveJob();

        return this.job;
    }

    protected abstract List<Stage> createStagesAndTasks();

    private void initJob() {
        Long clusterId = jobContext.getCommandDTO().getClusterId();
        this.cluster = clusterId == null ? new Cluster() : clusterRepository.getReferenceById(clusterId);

        this.job = new Job();
        job.setName(jobContext.getCommandDTO().getContext());
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
    }

    protected void saveJob() {
        jobRepository.save(job);

        for (int i = 0; i < job.getStages().size(); i++) {
            Stage stage = job.getStages().get(i);
            stage.setCluster(cluster);
            stage.setJob(job);
            stage.setStageOrder(i + 1);
            stageRepository.save(stage);

            for (Task task : stage.getTasks()) {
                task.setCluster(cluster);
                task.setJob(job);
                task.setStage(stage);
                taskRepository.save(task);
            }
        }
    }
}
