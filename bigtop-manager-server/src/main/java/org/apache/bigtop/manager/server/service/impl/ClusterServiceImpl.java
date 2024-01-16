package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.ClusterCreateJobFactory;
import org.apache.bigtop.manager.server.listener.factory.JobFactoryContext;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.event.ClusterCreateEvent;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.RepoRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.bigtop.manager.server.validate.ClusterCreateValidator;
import org.apache.bigtop.manager.server.validate.HostAddValidator;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private HostAddValidator hostAddValidator;

    @Resource
    private ClusterCreateValidator clusterCreateValidator;

    @Resource
    private StackRepository stackRepository;

    @Resource
    private ClusterCreateJobFactory clusterCreateJobFactory;

    @Resource
    private HostService hostService;

    @Override
    public List<ClusterVO> list() {
        List<ClusterVO> clusterVOList = new ArrayList<>();
        clusterRepository.findAll().forEach(cluster -> {
            ClusterVO clusterVO = ClusterMapper.INSTANCE.fromEntity2VO(cluster);
            clusterVOList.add(clusterVO);
        });

        return clusterVOList;
    }

    @Override
    @Transactional
    public CommandVO create(ClusterDTO clusterDTO) {
        String stackName = clusterDTO.getStackName();
        String stackVersion = clusterDTO.getStackVersion();

        // Check before create
        Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion);
        if (stack == null) {
            throw new ApiException(ApiExceptionEnum.STACK_NOT_FOUND);
        }

        clusterCreateValidator.validate(clusterDTO.getClusterName());

        // Check hosts
        List<String> hostnames = clusterDTO.getHostnames();
        hostAddValidator.validate(hostnames);

        // Create job
        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setClusterDTO(clusterDTO);
        Job job = clusterCreateJobFactory.createJob(jobFactoryContext);

        ClusterCreateEvent event = new ClusterCreateEvent(clusterDTO);
        event.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(event);
        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }


    @Override
    public ClusterVO save(ClusterDTO clusterDTO) {
        // Save cluster
        Stack stack = stackRepository.findByStackNameAndStackVersion(clusterDTO.getStackName(), clusterDTO.getStackVersion());
        StackDTO stackDTO = StackUtils.getStackKeyMap().get(StackUtils.fullStackName(clusterDTO.getStackName(), clusterDTO.getStackVersion())).getLeft();
        Cluster cluster = ClusterMapper.INSTANCE.fromDTO2Entity(clusterDTO, stackDTO, stack);
        cluster.setSelected(clusterRepository.count() == 0);
        cluster.setState(MaintainState.INSTALLED);

        Cluster oldCluster = clusterRepository.findByClusterName(clusterDTO.getClusterName()).orElse(new Cluster());
        if (oldCluster.getId() != null) {
            cluster.setId(oldCluster.getId());
        }
        clusterRepository.save(cluster);

        hostService.batchSave(cluster.getId(), clusterDTO.getHostnames());

        // Save repo
        List<Repo> repos = RepoMapper.INSTANCE.fromDTO2Entity(clusterDTO.getRepoInfoList(), cluster);
        List<Repo> oldRepos = repoRepository.findAllByCluster(cluster);

        for (Repo repo : repos) {
            for (Repo oldRepo : oldRepos) {
                if (oldRepo.getArch().equals(repo.getArch()) && oldRepo.getOs().equals(repo.getOs())) {
                    repo.setId(oldRepo.getId());
                }
            }
        }

        repoRepository.saveAll(repos);
        return ClusterMapper.INSTANCE.fromEntity2VO(cluster);
    }

    @Override
    public ClusterVO get(Long id) {
        Cluster cluster = clusterRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.CLUSTER_NOT_FOUND));

        return ClusterMapper.INSTANCE.fromEntity2VO(cluster);
    }

    @Override
    public ClusterVO update(Long id, ClusterDTO clusterDTO) {
        Cluster cluster = ClusterMapper.INSTANCE.fromDTO2Entity(clusterDTO);
        cluster.setId(id);
        clusterRepository.save(cluster);

        return ClusterMapper.INSTANCE.fromEntity2VO(cluster);
    }

}
