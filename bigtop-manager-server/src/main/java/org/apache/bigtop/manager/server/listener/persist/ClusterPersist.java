package org.apache.bigtop.manager.server.listener.persist;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.RepoRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClusterPersist {

    @Resource
    private StackRepository stackRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostPersist hostPersist;

    public Cluster persist(ClusterDTO clusterDTO) {
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

        hostPersist.persist(cluster, clusterDTO.getHostnames());

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
        return cluster;
    }
}
