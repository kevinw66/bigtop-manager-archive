package org.apache.bigtop.manager.server.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.ConfigDataDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ConfigurationMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.ConfigDataVO;
import org.apache.bigtop.manager.server.model.vo.StackComponentVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.service.StackService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class StackServiceImpl implements StackService {

    @Override
    public List<StackVO> list() {
        List<StackVO> stackVOList = new ArrayList<>();
        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        for (ImmutablePair<StackDTO, List<ServiceDTO>> pair : stackKeyMap.values()) {
            StackDTO stackDTO = pair.left;
            List<ServiceDTO> serviceDTOList = pair.right;

            StackVO stackVO = StackMapper.INSTANCE.fromDTO2VO(stackDTO);
            stackVO.setServices(ServiceMapper.INSTANCE.fromDTO2StackVO(serviceDTOList));
            stackVOList.add(stackVO);
        }

        return stackVOList;
    }

    @Override
    public List<StackComponentVO> components(String stackName, String stackVersion) {
        List<StackComponentVO> list = new ArrayList<>();

        ImmutablePair<StackDTO, List<ServiceDTO>> pair = StackUtils.getStackKeyMap().get(StackUtils.fullStackName(stackName, stackVersion));
        if (pair == null) {
            throw new ApiException(ApiExceptionEnum.STACK_NOT_FOUND);
        }

        List<ServiceDTO> serviceDTOList = pair.right;
        for (ServiceDTO serviceDTO : serviceDTOList) {
            list.addAll(ComponentMapper.INSTANCE.fromDTO2StackVO(serviceDTO.getComponents(), serviceDTO.getServiceName()));
        }

        return list;
    }

    @Override
    public Map<String, List<ConfigDataVO>> configurations(String stackName, String stackVersion) {
        Map<String, Map<String, Set<ConfigDataDTO>>> stackConfigMap = StackUtils.getStackConfigMap();
        Map<String, Set<ConfigDataDTO>> serviceConfigMap = stackConfigMap.get(StackUtils.fullStackName(stackName, stackVersion));

        Map<String, List<ConfigDataVO>> resultMap = new HashMap<>();
        for (Map.Entry<String, Set<ConfigDataDTO>> entry : serviceConfigMap.entrySet()) {
            Set<ConfigDataDTO> value = entry.getValue();
            List<ConfigDataVO> configDataVOS = value.stream().map(ConfigurationMapper.INSTANCE::fromDTO2VO).toList();
            resultMap.put(entry.getKey(), configDataVOS);
        }
        return resultMap;
    }
}
