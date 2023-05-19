package org.apache.bigtop.manager.server.service.impl;

import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private ClusterRepository clusterRepository;

    @Override
    public List<ClusterVO> list() {
        List<ClusterVO> clusterVOList = new ArrayList<>();
        clusterRepository.findAll().forEach(cluster -> {
            ClusterVO clusterVO = new ClusterVO();
            BeanUtils.copyProperties(cluster, clusterVO);
            clusterVOList.add(clusterVO);
        });

        return clusterVOList;
    }

    @Override
    public ClusterVO create(ClusterDTO clusterDTO) {
        Cluster cluster = new Cluster();
        BeanUtils.copyProperties(clusterDTO, cluster);
        clusterRepository.save(cluster);

        ClusterVO clusterVO = new ClusterVO();
        BeanUtils.copyProperties(cluster, clusterVO);
        return clusterVO;
    }

    @Override
    public ClusterVO get(Long id) {
        ClusterVO clusterVO = new ClusterVO();
        Cluster cluster = clusterRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.CLUSTER_NOT_FOUND));
        BeanUtils.copyProperties(cluster, clusterVO);
        return clusterVO;
    }

    @Override
    public ClusterVO update(Long id, ClusterDTO clusterDTO) {
        Cluster cluster = new Cluster();
        BeanUtils.copyProperties(clusterDTO, cluster);
        cluster.setId(id);
        clusterRepository.save(cluster);

        ClusterVO clusterVO = new ClusterVO();
        BeanUtils.copyProperties(cluster, clusterVO);
        return clusterVO;
    }

    @Override
    public Boolean delete(Long id) {
        clusterRepository.deleteById(id);
        return true;
    }
}
