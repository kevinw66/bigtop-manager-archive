package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.request.HostRequest;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HostMapper {

    HostMapper INSTANCE = Mappers.getMapper(HostMapper.class);

    HostDTO Request2DTO(HostRequest hostRequest);

    Host DTO2Entity(HostDTO hostDTO);

    HostVO Entity2VO(Host host);
}
