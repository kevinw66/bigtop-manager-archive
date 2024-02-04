package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.enums.JobState;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Job;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {StageMapper.class, TaskMapper.class})
public interface JobMapper {

    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    @Mapping(target = "cluster", expression = "java(cluster)")
    @Mapping(target = "state", expression = "java(initState())")
    Job fromDTO2Entity(CommandDTO commandDTO, @Context Cluster cluster);

    @Mapping(target = "cluster", expression = "java(cluster)")
    @Mapping(target = "state", expression = "java(initState())")
    Job fromDTO2Entity(HostDTO hostDTO, @Context Cluster cluster);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    JobVO fromEntity2VO(Job job);

    List<JobVO> fromEntity2VO(List<Job> job);

    CommandVO fromEntity2CommandVO(Job job);

    default JobState initState() {
        return JobState.PENDING;
    }

}
