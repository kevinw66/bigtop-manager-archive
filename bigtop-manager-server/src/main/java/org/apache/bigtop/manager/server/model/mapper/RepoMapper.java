package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.message.entity.pojo.RepoInfo;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Repo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RepoMapper {

    RepoMapper INSTANCE = Mappers.getMapper(RepoMapper.class);

    @Mapping(target = "cluster", expression = "java(cluster)")
    Repo fromDTO2Entity(RepoDTO repoDTO, @Context Cluster cluster);

    @Mapping(target = "cluster", expression = "java(cluster)")
    List<Repo> fromDTO2Entity(List<RepoDTO> repoDTOList, @Context Cluster cluster);

    RepoInfo fromEntity2Message(Repo repo);

    List<RepoInfo> fromDTO2Message(List<RepoDTO> repoDTOs);

}
