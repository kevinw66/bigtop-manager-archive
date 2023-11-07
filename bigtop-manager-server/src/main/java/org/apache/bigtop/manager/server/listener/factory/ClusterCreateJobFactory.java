package org.apache.bigtop.manager.server.listener.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.RepoRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.utils.StackUtils;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class ClusterCreateJobFactory implements JobFactory {

    @Resource
    private StackRepository stackRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private HostAddJobFactory hostAddJobFactory;

    public Job createJob(ClusterDTO clusterDTO) {

        Cluster cluster = saveCluster(clusterDTO);
        // Create job
        Job job = new Job();
        job.setContext("Create Cluster");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        hostAddJobFactory.createHostCheckStage(job, cluster, clusterDTO.getHostnames());

        return job;
    }

    private Cluster saveCluster(ClusterDTO clusterDTO) {
        // Save cluster
        Stack stack = stackRepository.findByStackNameAndStackVersion(clusterDTO.getStackName(), clusterDTO.getStackVersion());
        StackDTO stackDTO = StackUtils.getStackKeyMap().get(StackUtils.fullStackName(clusterDTO.getStackName(), clusterDTO.getStackVersion())).getLeft();
        Cluster cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO, stackDTO, stack);
        cluster.setSelected(clusterRepository.count() == 0);
        cluster.setState(MaintainState.UNINSTALLED);
        clusterRepository.save(cluster);

        hostAddJobFactory.saveHost(cluster, clusterDTO.getHostnames());

        // Save repo
        List<Repo> repos = RepoMapper.INSTANCE.DTO2Entity(clusterDTO.getRepoInfoList(), cluster);
        repoRepository.saveAll(repos);
        return cluster;
    }

}
