package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.HostCacheJobFactory;
import org.apache.bigtop.manager.server.listener.factory.JobFactoryContext;
import org.apache.bigtop.manager.server.model.dto.ConfigDataDTO;
import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.mapper.ConfigurationMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceConfigMappingRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.apache.bigtop.manager.server.stack.ConfigurationManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    private ConfigurationManager configurationManager;

    @Resource
    private HostCacheJobFactory hostCacheJobFactory;

    @Override
    public List<ServiceConfigVO> list(Long clusterId) {
        List<ServiceConfigMapping> serviceConfigMappingList = serviceConfigMappingRepository.findAllByServiceConfigRecordClusterId(clusterId);
        return ConfigurationMapper.INSTANCE.fromEntity2VO(serviceConfigMappingList);
    }

    @Override
    public List<ServiceConfigVO> latest(Long clusterId) {
        List<ServiceConfigMapping> resultList = serviceConfigMappingRepository.findAllGroupLastest(clusterId);
        return ConfigurationMapper.INSTANCE.fromEntity2VO(resultList);
    }

    @Override
    @Transactional
    public CommandVO update(Long clusterId, List<ConfigurationDTO> configurationDTOList) {
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        for (ConfigurationDTO configurationDTO : configurationDTOList) {
            updateConfig(cluster, configurationDTO);
        }

        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setClusterId(clusterId);
        Job job = hostCacheJobFactory.createJob(jobFactoryContext);

        HostCacheEvent hostCacheEvent = new HostCacheEvent(clusterId);
        hostCacheEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(hostCacheEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    private void updateConfig(Cluster cluster, ConfigurationDTO configurationDTO) {
        String serviceName = configurationDTO.getServiceName();
        Long clusterId = cluster.getId();

        Service service = serviceRepository.findByClusterIdAndServiceName(clusterId, serviceName).orElse(new Service());
        //ServiceConfigRecord
        ImmutablePair<ServiceConfigRecord, List<ServiceConfigMapping>> immutablePair = configurationManager.saveConfigRecord(cluster, service, configurationDTO.getConfigDesc());
        ServiceConfigRecord serviceConfigRecord = immutablePair.left;
        List<ServiceConfigMapping> serviceConfigMappingList = immutablePair.right;

        //ServiceConfig
        List<ConfigDataDTO> configurations = configurationDTO.getConfigurations();
        Map<String, ConfigDataDTO> configDataDTOMap = configurations.stream().collect(Collectors.toMap(ConfigDataDTO::getTypeName, configDataDTO -> configDataDTO));

        for (ServiceConfigMapping scp : serviceConfigMappingList) {
            ServiceConfig sc = scp.getServiceConfig();
            String typeName = sc.getTypeName();

            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            if (configDataDTOMap.containsKey(typeName)) {
                ConfigDataDTO configDataDTO = configDataDTOMap.get(typeName);
                Integer version = configDataDTO.getVersion();

                ServiceConfig serviceConfig;
                if (version != null) {
                    //rollback
                    serviceConfig = configurationManager.rollbackConfig(cluster, service, typeName, version);
                } else {
                    //upsert
                    serviceConfig = configurationManager.upsertConfig(cluster, service, typeName, configDataDTO.getProperties());
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

}
