package org.apache.bigtop.manager.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {

    private final ClusterRepository clusterRepository;

    @Override
    public List<ClusterVO> list() {
        List<ClusterVO> clusterVOList = new ArrayList<>();
        clusterRepository.findAll().forEach(cluster -> {
            ClusterVO clusterVO = ClusterMapper.INSTANCE.Entity2VO(cluster);
            clusterVOList.add(clusterVO);
        });

        return clusterVOList;
    }

    @Override
    public ClusterVO create(ClusterDTO clusterDTO) {
        Cluster cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO);
        clusterRepository.save(cluster);

        return ClusterMapper.INSTANCE.Entity2VO(cluster);
    }

    @Override
    public ClusterVO get(Long id) {
        Cluster cluster = clusterRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.CLUSTER_NOT_FOUND));

        return ClusterMapper.INSTANCE.Entity2VO(cluster);
    }

    @Override
    public ClusterVO update(Long id, ClusterDTO clusterDTO) {
        Cluster cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO);
        cluster.setId(id);
        clusterRepository.save(cluster);

        return ClusterMapper.INSTANCE.Entity2VO(cluster);
    }

    @Override
    public Boolean delete(Long id) {
        clusterRepository.deleteById(id);
        return true;
    }
}
