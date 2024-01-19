package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceConfigDTO;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;
import org.apache.bigtop.manager.server.model.mapper.ConfigMapper;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ServiceConfigMappingRepository serviceConfigMappingRepository;

    @Resource
    private ServiceConfigRecordRepository serviceConfigRecordRepository;

    @Resource
    private ServiceConfigRepository serviceConfigRepository;

    @Override
    public List<ServiceConfigVO> list(Long clusterId) {
        List<ServiceConfigMapping> serviceConfigMappingList = serviceConfigMappingRepository.findAllByServiceConfigRecordClusterId(clusterId);
        List<ServiceConfigVO> serviceConfigVOs = ConfigMapper.INSTANCE.fromEntity2VO(serviceConfigMappingList);
        serviceConfigVOs.sort(
                Comparator.comparing(ServiceConfigVO::getServiceName)
                        .thenComparing(ServiceConfigVO::getVersion).reversed()
        );
        return serviceConfigVOs;
    }

    @Override
    public List<ServiceConfigVO> latest(Long clusterId) {
        List<ServiceConfigMapping> resultList = serviceConfigMappingRepository.findAllGroupLastest(clusterId);
        return ConfigMapper.INSTANCE.fromEntity2VO(resultList);
    }

    @Override
    public void upsert(Long clusterId, Long serviceId, List<TypeConfigDTO> configs) {
        // Save config record
        Cluster cluster = clusterRepository.getReferenceById(clusterId);
        Service service = serviceRepository.getReferenceById(serviceId);
        String configDesc = "Initial config for " + service.getServiceName();
        ServiceConfigRecord serviceConfigRecord = saveConfigRecord(cluster, service, configDesc).left;

        for (TypeConfigDTO typeConfigDTO : configs) {
            String typeName = typeConfigDTO.getTypeName();
            List<PropertyDTO> properties = typeConfigDTO.getProperties();

            ServiceConfig serviceConfig = upsertConfig(cluster, service, typeName, properties);

            // Save config mapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }
    }

    @Override
    public void updateConfig(Cluster cluster, ServiceConfigDTO serviceConfigDTO) {
        String serviceName = serviceConfigDTO.getServiceName();
        Long clusterId = cluster.getId();

        Service service = serviceRepository.findByClusterIdAndServiceName(clusterId, serviceName).orElse(new Service());
        //ServiceConfigRecord
        ImmutablePair<ServiceConfigRecord, List<ServiceConfigMapping>> immutablePair = saveConfigRecord(cluster, service, serviceConfigDTO.getConfigDesc());
        ServiceConfigRecord serviceConfigRecord = immutablePair.left;
        List<ServiceConfigMapping> serviceConfigMappingList = immutablePair.right;

        //ServiceConfig
        List<TypeConfigDTO> configurations = serviceConfigDTO.getConfigs();
        Map<String, TypeConfigDTO> typeConfigMap = configurations.stream().collect(Collectors.toMap(TypeConfigDTO::getTypeName, Function.identity()));

        for (ServiceConfigMapping scp : serviceConfigMappingList) {
            ServiceConfig sc = scp.getServiceConfig();
            String typeName = sc.getTypeName();

            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            if (typeConfigMap.containsKey(typeName)) {
                TypeConfigDTO typeConfigDTO = typeConfigMap.get(typeName);
                Integer version = typeConfigDTO.getVersion();

                ServiceConfig serviceConfig;
                if (version != null) {
                    //rollback
                    serviceConfig = rollbackConfig(cluster, service, typeName, version);
                } else {
                    //upsert
                    serviceConfig = upsertConfig(cluster, service, typeName, typeConfigDTO.getProperties());
                }

                serviceConfigMapping.setServiceConfig(serviceConfig);
            } else {
                log.info("Does not contain {}, supplementary mapping relationship", scp.getServiceConfig().getTypeName());
                serviceConfigMapping.setServiceConfig(sc);
            }
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }

    }

    public ImmutablePair<ServiceConfigRecord, List<ServiceConfigMapping>> saveConfigRecord(Cluster cluster, Service service, String configDesc) {
        ServiceConfigRecord latestServiceConfigRecord = serviceConfigRecordRepository
                .findFirstByClusterIdAndServiceIdOrderByVersionDesc(cluster.getId(), service.getId())
                .orElse(new ServiceConfigRecord());
        List<ServiceConfigMapping> serviceConfigMappingList = new ArrayList<>();

        ServiceConfigRecord serviceConfigRecord = new ServiceConfigRecord();
        if (latestServiceConfigRecord.getId() != null) {
            serviceConfigMappingList = serviceConfigMappingRepository.findAllByServiceConfigRecordId(latestServiceConfigRecord.getId());
            serviceConfigRecord.setVersion(latestServiceConfigRecord.getVersion() + 1);
        } else {
            serviceConfigRecord.setVersion(1);
        }
        serviceConfigRecord.setConfigDesc(configDesc);
        serviceConfigRecord.setService(service);
        serviceConfigRecord.setCluster(cluster);
        serviceConfigRecord = serviceConfigRecordRepository.save(serviceConfigRecord);
        return new ImmutablePair<>(serviceConfigRecord, serviceConfigMappingList);
    }

    /**
     * rollback configuration
     */
    public ServiceConfig rollbackConfig(Cluster cluster, Service service, String typeName, Integer version) {
        ServiceConfig serviceConfig = serviceConfigRepository.findFirstByClusterIdAndServiceIdAndTypeNameAndVersion(cluster.getId(), service.getId(), typeName, version)
                .orElse(new ServiceConfig());

        if (serviceConfig.getId() == null) {
            log.error("Rollback configuration failed, version {} for ServiceConfig does not exist", version);
            throw new ApiException(ApiExceptionEnum.CONFIG_NOT_FOUND);
        }

        return serviceConfig;
    }

    /**
     * add|update configuration
     */
    public ServiceConfig upsertConfig(Cluster cluster, Service service, String typeName, List<PropertyDTO> properties) {
        ServiceConfig serviceConfig = new ServiceConfig();

        ServiceConfig latestServiceConfig = serviceConfigRepository
                .findFirstByClusterIdAndServiceIdAndTypeNameOrderByVersionDesc(cluster.getId(), service.getId(), typeName)
                .orElse(new ServiceConfig());

        log.debug("The latest version of the configuration saved in database: {}", latestServiceConfig);
        serviceConfig.setService(service);
        serviceConfig.setCluster(cluster);
        serviceConfig.setTypeName(typeName);

        String propertiesJson = JsonUtils.writeAsString(properties);
        serviceConfig.setPropertiesJson(propertiesJson);

        if (latestServiceConfig.getId() == null) {
            log.info("Insert serviceConfig");
            serviceConfig.setVersion(1);
            serviceConfig = serviceConfigRepository.save(serviceConfig);
        } else if (!propertiesJson.equals(latestServiceConfig.getPropertiesJson())) {
            log.info("Update serviceConfig");
            serviceConfig.setVersion(latestServiceConfig.getVersion() + 1);
            serviceConfig = serviceConfigRepository.save(serviceConfig);
        } else {
            serviceConfig = latestServiceConfig;
            log.info("No need to update configuration");
        }
        return serviceConfig;
    }

}
