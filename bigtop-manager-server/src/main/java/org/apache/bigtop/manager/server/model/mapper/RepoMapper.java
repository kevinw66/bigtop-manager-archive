package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RepoMapper {

    RepoMapper INSTANCE = Mappers.getMapper(RepoMapper.class);

    StackRepoVO DTO2VO(RepoDTO repoDTO);

    @Mapping(target = "stack", expression = "java(stack)")
    Repo DTO2Entity(RepoDTO repoDTO, @Context Stack stack);

    RepoInfo Entity2Message(Repo repo);

}
