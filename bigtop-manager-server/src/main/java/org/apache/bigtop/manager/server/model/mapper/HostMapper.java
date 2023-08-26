package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.request.HostRequest;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostMapper {

    HostMapper INSTANCE = Mappers.getMapper(HostMapper.class);

    HostDTO Request2DTO(HostRequest hostRequest);

    Host DTO2Entity(HostDTO hostDTO);

    Host Message2Entity(HostInfo hostInfo);

    @Mapping(target = "clusterId", source = "cluster.id")
    HostVO Entity2VO(Host host);

    HostDTO Entity2DTO(Host host);
}
