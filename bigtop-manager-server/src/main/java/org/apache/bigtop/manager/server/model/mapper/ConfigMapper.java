package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.utils.DateUtils;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceConfigDTO;
import org.apache.bigtop.manager.server.model.req.ServiceConfigReq;
import org.apache.bigtop.manager.server.model.vo.TypeConfigVO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfigMapping;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfigRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper
public interface ConfigMapper {

    ConfigMapper INSTANCE = Mappers.getMapper(ConfigMapper.class);

    ServiceConfigDTO fromReq2DTO(ServiceConfigReq serviceConfigReq);

    List<ServiceConfigDTO> fromReq2DTO(List<ServiceConfigReq> serviceConfigReqs);

    TypeConfigVO fromDTO2VO(TypeConfigDTO typeConfigDTO);

    List<TypeConfigVO> fromDTO2VO(Collection<TypeConfigDTO> typeConfigDTOS);

    default List<ServiceConfigVO> fromEntity2VO(List<ServiceConfigMapping> serviceConfigMappings) {
        Map<ServiceConfigRecord, ServiceConfigVO> map = new HashMap<>();

        for (ServiceConfigMapping serviceConfigMapping : serviceConfigMappings) {
            ServiceConfig serviceConfig = serviceConfigMapping.getServiceConfig();
            ServiceConfigRecord serviceConfigRecord = serviceConfigMapping.getServiceConfigRecord();

            ServiceConfigVO serviceConfigVO = map.getOrDefault(serviceConfigRecord, new ServiceConfigVO());
            map.put(serviceConfigRecord, serviceConfigVO);
            List<TypeConfigVO> typeConfigVOList = serviceConfigVO.getConfigs() == null ? new ArrayList<>() : serviceConfigVO.getConfigs();

            TypeConfigVO typeConfigVO = new TypeConfigVO();
            typeConfigVO.setProperties(JsonUtils.readFromString(serviceConfig.getPropertiesJson()));
            typeConfigVO.setVersion(serviceConfig.getVersion());
            typeConfigVO.setTypeName(serviceConfig.getTypeName());
            typeConfigVOList.add(typeConfigVO);

            serviceConfigVO.setConfigs(typeConfigVOList);
            serviceConfigVO.setConfigDesc(serviceConfigRecord.getConfigDesc());
            serviceConfigVO.setVersion(serviceConfigRecord.getVersion());
            serviceConfigVO.setServiceName(serviceConfigRecord.getService().getServiceName());
            serviceConfigVO.setCreateTime(DateUtils.format(serviceConfigRecord.getCreateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
            serviceConfigVO.setUpdateTime(DateUtils.format(serviceConfigRecord.getUpdateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        }

        return new ArrayList<>(map.values());
    }
}
