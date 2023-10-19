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

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface RepoMapper {

    RepoMapper INSTANCE = Mappers.getMapper(RepoMapper.class);

    StackRepoVO DTO2VO(RepoDTO repoDTO);

    @Mapping(target = "cluster", expression = "java(cluster)")
    Repo DTO2Entity(RepoDTO repoDTO, @Context Cluster cluster);

    default List<Repo> DTO2Entity(List<RepoDTO> repoDTOList, Cluster cluster) {
        List<Repo> res = new ArrayList<>();
        for (RepoDTO repoDTO : repoDTOList) {
            res.add(DTO2Entity(repoDTO, cluster));
        }

        return res;
    }

    RepoInfo Entity2Message(Repo repo);

}
