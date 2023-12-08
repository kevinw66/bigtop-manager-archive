package org.apache.bigtop.manager.server.model.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.ConfigDataDTO;
import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.req.ConfigurationReq;
import org.apache.bigtop.manager.server.model.vo.ConfigDataVO;
import org.apache.bigtop.manager.server.model.vo.ConfigurationVO;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfigMapping;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfigRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConfigurationMapper {

    ConfigurationMapper INSTANCE = Mappers.getMapper(ConfigurationMapper.class);

    ConfigurationDTO fromReq2DTO(ConfigurationReq configurationReq);

    List<ConfigurationDTO> fromReq2DTO(List<ConfigurationReq> configurationReqs);

    ConfigDataVO fromDTO2VO(ConfigDataDTO configDataDTO);

    default List<ConfigurationVO> fromEntity2VO(List<ServiceConfigMapping> serviceConfigMappings) {
        Map<ServiceConfigRecord, ConfigurationVO> map = new HashMap<>();

        for (ServiceConfigMapping serviceConfigMapping : serviceConfigMappings) {
            ServiceConfig serviceConfig = serviceConfigMapping.getServiceConfig();
            ServiceConfigRecord serviceConfigRecord = serviceConfigMapping.getServiceConfigRecord();

            ConfigurationVO configurationVO = map.getOrDefault(serviceConfigRecord, new ConfigurationVO());
            map.put(serviceConfigRecord, configurationVO);
            List<ConfigDataVO> configDataVOList = configurationVO.getConfigurations() == null ? new ArrayList<>() : configurationVO.getConfigurations();

            ConfigDataVO configDataVO = new ConfigDataVO();
            String attributes = serviceConfig.getAttributes();
            if (attributes != null) {
                configDataVO.setAttributes(JsonUtils.readFromString(attributes, new TypeReference<>() {
                }));
            }
            configDataVO.setProperties(JsonUtils.readFromString(serviceConfig.getConfigData(), new TypeReference<>() {
            }));
            configDataVO.setVersion(serviceConfig.getVersion());
            configDataVO.setTypeName(serviceConfig.getTypeName());
            configDataVOList.add(configDataVO);

            configurationVO.setConfigurations(configDataVOList);
            configurationVO.setConfigDesc(serviceConfigRecord.getConfigDesc());
            configurationVO.setVersion(serviceConfigRecord.getVersion());
            configurationVO.setServiceName(serviceConfigRecord.getService().getServiceName());
            configurationVO.setClusterName(serviceConfigRecord.getCluster().getClusterName());
        }

        return new ArrayList<>(map.values());
    }
}
