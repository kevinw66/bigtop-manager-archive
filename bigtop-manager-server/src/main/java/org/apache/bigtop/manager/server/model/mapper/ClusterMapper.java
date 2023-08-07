package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.pojo.stack.StackInfo;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.request.ClusterRequest;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClusterMapper {

    ClusterMapper INSTANCE = Mappers.getMapper(ClusterMapper.class);

    ClusterDTO Request2DTO(ClusterRequest clusterRequest);

    Cluster DTO2Entity(ClusterDTO clusterDTO);

    @Mapping(target = "stackName", source = "cluster.stack.stackName")
    @Mapping(target = "stackVersion", source = "cluster.stack.stackVersion")
    ClusterVO Entity2VO(Cluster cluster);

    @Mapping(target = "stack.stackName", source = "clusterDTO.stackName")
    @Mapping(target = "stack.stackVersion", source = "clusterDTO.stackVersion")
    Cluster DTO2Entity(ClusterDTO clusterDTO, StackInfo clusterModel);
}
