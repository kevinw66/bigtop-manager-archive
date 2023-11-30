package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.req.ClusterReq;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClusterMapper {

    ClusterMapper INSTANCE = Mappers.getMapper(ClusterMapper.class);

    ClusterDTO fromReq2DTO(ClusterReq clusterReq);

    Cluster fromDTO2Entity(ClusterDTO clusterDTO);

    @Mapping(target = "stackName", source = "cluster.stack.stackName")
    @Mapping(target = "stackVersion", source = "cluster.stack.stackVersion")
    ClusterVO fromEntity2VO(Cluster cluster);

    @Mapping(target = "stack", expression = "java(stack)")
    Cluster fromDTO2Entity(ClusterDTO clusterDTO, StackDTO stackDTO, @Context Stack stack);
}
