package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JobMapper {

    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    @Mapping(target = "cluster", expression = "java(cluster)")
    @Mapping(target = "state", expression = "java(initState())")
    Job DTO2Entity(CommandDTO commandDTO, @Context Cluster cluster);

    @Mapping(target = "cluster", expression = "java(cluster)")
    @Mapping(target = "state", expression = "java(initState())")
    Job DTO2Entity(HostDTO hostDTO, @Context Cluster cluster);

    CommandVO Entity2VO(Job job);

    default JobState initState() {
        return JobState.PENDING;
    }

}
