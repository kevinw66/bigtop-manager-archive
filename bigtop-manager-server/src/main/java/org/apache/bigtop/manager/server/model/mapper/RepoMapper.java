package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.pojo.stack.RepoInfo;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RepoMapper {

    RepoMapper INSTANCE = Mappers.getMapper(RepoMapper.class);

    @Mapping(target = "stack", expression = "java(stack)")
    Repo DTO2Entity(RepoInfo repoInfo, @Context Stack stack);

    List<Repo> DTO2Entity(List<RepoInfo> repoInfoList, @Context Stack stack);

}
