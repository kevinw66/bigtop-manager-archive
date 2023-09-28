package org.apache.bigtop.manager.server.service.impl;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
import org.apache.bigtop.manager.server.model.vo.StackServiceVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.service.StackService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StackServiceImpl implements StackService {

    @Resource
    private StackRepository stackRepository;

    @Override
    public List<StackVO> list() {
        List<StackVO> stackVOList = new ArrayList<>();
        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        for (ImmutablePair<StackDTO, List<ServiceDTO>> pair : stackKeyMap.values()) {
            StackDTO stackDTO = pair.left;
            List<ServiceDTO> serviceDTOList = pair.right;

            StackVO stackVO = StackMapper.INSTANCE.DTO2VO(stackDTO);
            stackVO.setServices(ServiceMapper.INSTANCE.DTOList2VOList(serviceDTOList));
            stackVOList.add(stackVO);
        }

        return stackVOList;
    }
}
