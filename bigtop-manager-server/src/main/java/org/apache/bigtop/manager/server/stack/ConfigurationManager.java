package org.apache.bigtop.manager.server.stack;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.ServiceConfigMappingRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceConfigRecordRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceConfigRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ConfigurationManager {

    @Resource
    private ServiceConfigMappingRepository serviceConfigMappingRepository;

    @Resource
    private ServiceConfigRecordRepository serviceConfigRecordRepository;

    @Resource
    private ServiceConfigRepository serviceConfigRepository;

    public ImmutablePair<ServiceConfigRecord, List<ServiceConfigMapping>> saveConfigRecord(Cluster cluster, Service service, String configDesc) {
        ServiceConfigRecord latestServiceConfigRecord = serviceConfigRecordRepository.findFirstByClusterIdAndServiceIdOrderByVersionDesc(cluster.getId(), service.getId())
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
            throw new ApiException(ApiExceptionEnum.CONFIGURATION_NOT_FOUND);
        }

        return serviceConfig;
    }

    /**
     * add|update configuration
     */
    public ServiceConfig upsertConfig(Cluster cluster, Service service, String typeName, Map<String, Object> configData, Map<String, Map<String, Object>> configAttributes) {
        ServiceConfig serviceConfig = new ServiceConfig();

        ServiceConfig latestServiceConfig = serviceConfigRepository.findFirstByClusterIdAndServiceIdAndTypeNameOrderByVersionDesc(cluster.getId(), service.getId(), typeName)
                .orElse(new ServiceConfig());

        log.debug("The latest version of the configuration saved in database: {}", latestServiceConfig);
        serviceConfig.setService(service);
        serviceConfig.setCluster(cluster);
        serviceConfig.setTypeName(typeName);
        String configDataStr = JsonUtils.writeAsString(configData);
        serviceConfig.setConfigData(configDataStr);
        serviceConfig.setConfigAttributes(JsonUtils.writeAsString(configAttributes));

        if (latestServiceConfig.getId() == null) {
            log.info("Insert serviceConfig");
            serviceConfig.setVersion(1);
            serviceConfig = serviceConfigRepository.save(serviceConfig);
        } else if (!configDataStr.equals(latestServiceConfig.getConfigData())) {
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
