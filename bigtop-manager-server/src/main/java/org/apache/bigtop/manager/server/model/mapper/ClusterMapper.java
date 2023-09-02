package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.req.ClusterReq;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClusterMapper {

    ClusterMapper INSTANCE = Mappers.getMapper(ClusterMapper.class);

    ClusterDTO Req2DTO(ClusterReq clusterReq);

    Cluster DTO2Entity(ClusterDTO clusterDTO);

    @Mapping(target = "stackName", source = "cluster.stack.stackName")
    @Mapping(target = "stackVersion", source = "cluster.stack.stackVersion")
    ClusterVO Entity2VO(Cluster cluster);

    @Mapping(target = "stack", expression = "java(stack)")
    Cluster DTO2Entity(ClusterDTO clusterDTO, StackDTO stackDTO, Stack stack);
}
