package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ConfigDataDTO;
import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.mapper.ConfigurationMapper;
import org.apache.bigtop.manager.server.model.vo.ConfigurationVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.ConfigurationService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class ConfigurationServiceImpl implements ConfigurationService {

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
    public List<ConfigurationVO> list(String clusterName) {
        List<ServiceConfigMapping> serviceConfigMappingList = serviceConfigMappingRepository.findAllByServiceConfigRecordClusterClusterName(clusterName);
        return ConfigurationMapper.INSTANCE.Entity2VO(serviceConfigMappingList);
    }

    @Override
    public List<ConfigurationVO> latest(String clusterName) {
        List<ServiceConfigMapping> resultList = serviceConfigMappingRepository.findAllGroupLastest(clusterName);
        return ConfigurationMapper.INSTANCE.Entity2VO(resultList);
    }

    @Override
    public List<ConfigurationVO> update(String clusterName, List<ConfigurationDTO> configurationDTOList) {
        List<ServiceConfigMapping> serviceConfigMappingList = new ArrayList<>();
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());

        for (ConfigurationDTO configurationDTO : configurationDTOList) {
            updateConfig(cluster, configurationDTO, serviceConfigMappingList);
        }

        return ConfigurationMapper.INSTANCE.Entity2VO(serviceConfigMappingList);
    }

    private void updateConfig(Cluster cluster, ConfigurationDTO configurationDTO, List<ServiceConfigMapping> serviceConfigMappingList) {
        List<String> typeNameList = new ArrayList<>();
        String serviceName = configurationDTO.getServiceName();

        Service service = serviceRepository.findByServiceName(serviceName).orElse(new Service());
        //ServiceConfigRecord
        ServiceConfigRecord latestServiceConfigRecord = serviceConfigRecordRepository.findFirstByClusterIdAndServiceIdOrderByVersionDesc(cluster.getId(), service.getId())
                .orElse(new ServiceConfigRecord());

        ServiceConfigRecord serviceConfigRecord = new ServiceConfigRecord();
        if (latestServiceConfigRecord.getId() != null) {
            serviceConfigRecord.setVersion(latestServiceConfigRecord.getVersion() + 1);
        } else {
            serviceConfigRecord.setVersion(1);
        }
        serviceConfigRecord.setConfigDesc(configurationDTO.getConfigDesc());
        serviceConfigRecord.setService(service);
        serviceConfigRecord.setCluster(cluster);
        log.debug("serviceConfigRecord: " + serviceConfigRecord);
        serviceConfigRecord = serviceConfigRecordRepository.save(serviceConfigRecord);

        //ServiceConfig
        List<ConfigDataDTO> configurations = configurationDTO.getConfigurations();
        for (ConfigDataDTO configDataDTO : configurations) {
            String configData = JsonUtils.object2String(configDataDTO.getConfigData());
            String configAttributes = configDataDTO.getConfigAttributes() != null ? JsonUtils.object2String(configDataDTO.getConfigAttributes()) : null;
            String typeName = configDataDTO.getTypeName();
            Integer version = configDataDTO.getVersion();

            ServiceConfig serviceConfig = new ServiceConfig();
            if (version != null) { //回滚
                serviceConfig = serviceConfigRepository.findFirstByClusterIdAndServiceIdAndTypeNameAndVersion(cluster.getId(), service.getId(), typeName, version)
                        .orElseThrow(() -> new ServerException("version not found"));
                System.err.println("回滚配置serviceConfig: " + serviceConfig);
                if (!StringUtils.equals(configData, serviceConfig.getConfigData())) {
                    log.warn("回滚配置失败, configData 不一致");
                    break;
                }
                if (configAttributes != null && serviceConfig.getConfigAttributes() != null) {
                    if (!configAttributes.equals(serviceConfig.getConfigAttributes())) {
                        log.warn("回滚配置失败, configAttributes 不一致");
                        break;
                    }
                }
                if (!(configAttributes == null && serviceConfig.getConfigAttributes() == null)) {
                    log.warn("回滚配置失败, configAttributes 不一致为null");
                    break;
                }
            } else { //新增或更新
                ServiceConfig latestServiceConfig = serviceConfigRepository.findFirstByClusterIdAndServiceIdAndTypeNameOrderByVersionDesc(cluster.getId(), service.getId(), typeName)
                        .orElse(new ServiceConfig());

                log.debug("db中保存的最新版本的配置latestServiceConfig: {}", latestServiceConfig);
                serviceConfig.setService(service);
                serviceConfig.setCluster(cluster);
                serviceConfig.setTypeName(typeName);
                serviceConfig.setConfigData(configData);

                if (latestServiceConfig.getId() == null) {
                    log.info("insert serviceConfig");
                    serviceConfig.setVersion(1);
                    serviceConfig = serviceConfigRepository.save(serviceConfig);
                } else if (!configData.equals(latestServiceConfig.getConfigData())) {
                    log.info("update serviceConfig");
                    serviceConfig.setVersion(latestServiceConfig.getVersion() + 1);
                    serviceConfig = serviceConfigRepository.save(serviceConfig);
                } else {
                    serviceConfig = latestServiceConfig;
                    log.info("don't need update serviceConfig");
                }
            }

            //ServiceConfigMapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMapping = serviceConfigMappingRepository.save(serviceConfigMapping);

            serviceConfigMappingList.add(serviceConfigMapping);
            typeNameList.add(typeName);
        }

        //fill up the missing typeName for service
        List<ServiceConfigMapping> serviceConfigMappingList1 = serviceConfigMappingRepository.findAllByServiceConfigRecordId(latestServiceConfigRecord.getId());
        for (ServiceConfigMapping scp : serviceConfigMappingList1) {

            if (!typeNameList.contains(scp.getServiceConfig().getTypeName())) {
                log.info("不包含 {}， 补充映射关系", scp.getServiceConfig().getTypeName());
                ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
                serviceConfigMapping.setServiceConfig(scp.getServiceConfig());
                serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
                serviceConfigMappingRepository.save(serviceConfigMapping);
            }
        }
    }

}
