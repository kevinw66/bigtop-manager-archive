package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.req.HostReq;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HostMapper {

    HostMapper INSTANCE = Mappers.getMapper(HostMapper.class);

    HostDTO fromReq2DTO(HostReq hostReq);

    Host fromDTO2Entity(HostDTO hostDTO);

    @Mapping(target = "clusterName", source = "cluster.clusterName")
    HostVO fromEntity2VO(Host host);

    List<HostVO> fromEntity2VO(List<Host> hosts);

}
