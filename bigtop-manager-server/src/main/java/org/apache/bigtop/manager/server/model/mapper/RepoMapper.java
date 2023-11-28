package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RepoMapper {

    RepoMapper INSTANCE = Mappers.getMapper(RepoMapper.class);

    StackRepoVO DTO2VO(RepoDTO repoDTO);

    @Mapping(target = "cluster", expression = "java(cluster)")
    Repo DTO2Entity(RepoDTO repoDTO, @Context Cluster cluster);

    @Mapping(target = "cluster", expression = "java(cluster)")
    List<Repo> DTO2Entity(List<RepoDTO> repoDTOList, @Context Cluster cluster);

    RepoInfo Entity2Message(Repo repo);

    List<RepoInfo> DTO2Message(List<RepoDTO> repoDTOs);

}
