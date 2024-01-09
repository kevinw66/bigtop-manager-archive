package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.ConfigDataDTO;
import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.req.ConfigurationReq;
import org.apache.bigtop.manager.server.model.vo.ConfigDataVO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfigMapping;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfigRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper
public interface ConfigurationMapper {

    ConfigurationMapper INSTANCE = Mappers.getMapper(ConfigurationMapper.class);

    ConfigurationDTO fromReq2DTO(ConfigurationReq configurationReq);

    List<ConfigurationDTO> fromReq2DTO(List<ConfigurationReq> configurationReqs);

    ConfigDataVO fromDTO2VO(ConfigDataDTO configDataDTO);

    List<ConfigDataVO> fromDTO2VO(Collection<ConfigDataDTO> configDataDTOs);

    default List<ServiceConfigVO> fromEntity2VO(List<ServiceConfigMapping> serviceConfigMappings) {
        Map<ServiceConfigRecord, ServiceConfigVO> map = new HashMap<>();

        for (ServiceConfigMapping serviceConfigMapping : serviceConfigMappings) {
            ServiceConfig serviceConfig = serviceConfigMapping.getServiceConfig();
            ServiceConfigRecord serviceConfigRecord = serviceConfigMapping.getServiceConfigRecord();

            ServiceConfigVO serviceConfigVO = map.getOrDefault(serviceConfigRecord, new ServiceConfigVO());
            map.put(serviceConfigRecord, serviceConfigVO);
            List<ConfigDataVO> configDataVOList = serviceConfigVO.getConfigs() == null ? new ArrayList<>() : serviceConfigVO.getConfigs();

            ConfigDataVO configDataVO = new ConfigDataVO();
            configDataVO.setProperties(JsonUtils.readFromString(serviceConfig.getPropertiesJson()));
            configDataVO.setVersion(serviceConfig.getVersion());
            configDataVO.setTypeName(serviceConfig.getTypeName());
            configDataVOList.add(configDataVO);

            serviceConfigVO.setConfigs(configDataVOList);
            serviceConfigVO.setConfigDesc(serviceConfigRecord.getConfigDesc());
            serviceConfigVO.setVersion(serviceConfigRecord.getVersion());
            serviceConfigVO.setServiceName(serviceConfigRecord.getService().getServiceName());
        }

        return new ArrayList<>(map.values());
    }
}
