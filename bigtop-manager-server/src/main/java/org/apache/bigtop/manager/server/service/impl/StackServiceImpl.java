package org.apache.bigtop.manager.server.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.ConfigDataDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ConfigurationMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.ConfigDataVO;
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

            StackVO stackVO = StackMapper.INSTANCE.DTO2VO(stackDTO);
            stackVO.setServices(ServiceMapper.INSTANCE.DTO2VO(serviceDTOList));
            stackVOList.add(stackVO);
        }

        return stackVOList;
    }

    @Override
    public Map<String, List<ConfigDataVO>> configurations(String stackName, String stackVersion) {
        Map<String, Map<String, Set<ConfigDataDTO>>> stackConfigMap = StackUtils.getStackConfigMap();
        Map<String, Set<ConfigDataDTO>> serviceConfigMap = stackConfigMap.get(StackUtils.fullStackName(stackName, stackVersion));

        Map<String, List<ConfigDataVO>> resultMap = new HashMap<>();
        for (Map.Entry<String, Set<ConfigDataDTO>> entry : serviceConfigMap.entrySet()) {
            Set<ConfigDataDTO> value = entry.getValue();
            List<ConfigDataVO> configDataVOS = value.stream().map(ConfigurationMapper.INSTANCE::DTO2VO).toList();
            resultMap.put(entry.getKey(), configDataVOS);
        }
        return resultMap;
    }
}
