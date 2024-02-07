package org.apache.bigtop.manager.server.command.job.runner.cluster;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.command.job.runner.AbstractJobRunner;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.entity.Task;
import org.apache.bigtop.manager.dao.repository.*;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClusterCreateJobRunner extends AbstractJobRunner {

    @Resource
    private ClusterService clusterService;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.CLUSTER, Command.CREATE);
    }

    @Override
    public void onSuccess() {
        super.onSuccess();

        // Save Cluster
        CommandDTO commandDTO = JsonUtils.readFromString(job.getPayload(), CommandDTO.class);
        ClusterDTO clusterDTO = ClusterMapper.INSTANCE.fromCommand2DTO(commandDTO.getClusterCommand());
        clusterService.save(clusterDTO);

        // Link job to cluster after cluster successfully added
        Cluster cluster = clusterRepository.findByClusterName(commandDTO.getClusterCommand().getClusterName()).orElse(new Cluster());
        job.setCluster(cluster);
        jobRepository.save(job);

        for (Stage stage : job.getStages()) {
            stage.setCluster(cluster);
            stageRepository.save(stage);

            for (Task task : stage.getTasks()) {
                task.setCluster(cluster);
                taskRepository.save(task);
            }
        }
    }
}
