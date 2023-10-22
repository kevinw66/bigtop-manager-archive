package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
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

    HostDTO Req2DTO(HostReq hostReq);

    Host DTO2Entity(HostDTO hostDTO);

    Host Message2Entity(HostInfo hostInfo);

    @Mapping(target = "clusterName", source = "cluster.clusterName")
    HostVO Entity2VO(Host host);

    List<HostVO> Entity2VO(List<Host> hosts);

    HostDTO Entity2DTO(Host host);
}
