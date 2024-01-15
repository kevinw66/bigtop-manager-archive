package org.apache.bigtop.manager.server.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ConfigMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.ServiceComponentVO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.service.StackService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            stackVO.setServices(ServiceMapper.INSTANCE.fromDTO2VO(serviceDTOList));
            stackVOList.add(stackVO);
        }

        return stackVOList;
    }

    @Override
    public List<ServiceComponentVO> components(String stackName, String stackVersion) {
        List<ServiceComponentVO> list = new ArrayList<>();

        ImmutablePair<StackDTO, List<ServiceDTO>> pair = StackUtils.getStackKeyMap().get(StackUtils.fullStackName(stackName, stackVersion));
        if (pair == null) {
            throw new ApiException(ApiExceptionEnum.STACK_NOT_FOUND);
        }

        List<ServiceDTO> serviceDTOList = pair.right;
        for (ServiceDTO serviceDTO : serviceDTOList) {
            ServiceComponentVO element = new ServiceComponentVO();
            element.setServiceName(serviceDTO.getServiceName());
            element.setComponents(ComponentMapper.INSTANCE.fromDTO2VO(serviceDTO.getComponents()));
            list.add(element);
        }

        return list;
    }

    @Override
    public List<ServiceConfigVO> configurations(String stackName, String stackVersion) {
        List<ServiceConfigVO> list = new ArrayList<>();
        Map<String, Map<String, Set<TypeConfigDTO>>> stackConfigMap = StackUtils.getStackConfigMap();
        Map<String, Set<TypeConfigDTO>> serviceConfigMap = stackConfigMap.get(StackUtils.fullStackName(stackName, stackVersion));

        for (Map.Entry<String, Set<TypeConfigDTO>> entry : serviceConfigMap.entrySet()) {
            ServiceConfigVO element = new ServiceConfigVO();
            element.setServiceName(entry.getKey());
            element.setConfigs(ConfigMapper.INSTANCE.fromDTO2VO(entry.getValue()));
            list.add(element);
        }

        return list;
    }
}
