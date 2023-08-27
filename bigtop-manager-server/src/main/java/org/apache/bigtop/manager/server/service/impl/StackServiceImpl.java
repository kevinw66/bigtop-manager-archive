package org.apache.bigtop.manager.server.service.impl;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
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
import java.util.Set;

@Slf4j
@Service
public class StackServiceImpl implements StackService {

    @Resource
    private StackRepository stackRepository;

    @Override
    public List<StackVO> list() {
        List<StackVO> stackVOList = new ArrayList<>();
        stackRepository.findAll().forEach(stack -> {
            StackVO stackVO = StackMapper.INSTANCE.Entity2VO(stack);
            stackVOList.add(stackVO);
        });

        return stackVOList;
    }

    @Override
    public StackVO get(Long id) {
        Stack stack = stackRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.STACK_NOT_FOUND));

        return StackMapper.INSTANCE.Entity2VO(stack);
    }

    @Override
    public List<ServiceVersionVO> versions(String stackName, String stackVersion) {
        Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);

        ImmutablePair<StackDTO, Set<ServiceDTO>> stackDTOSetImmutablePair = stackKeyMap.get(fullStackName);

        List<ServiceVersionVO> serviceVersionVOList = new ArrayList<>();
        for (ServiceDTO serviceDTO : stackDTOSetImmutablePair.right) {
            ServiceVersionVO serviceVersionVO = ServiceMapper.INSTANCE.DTO2VO(serviceDTO);
            serviceVersionVOList.add(serviceVersionVO);
        }

        return serviceVersionVOList;
    }

    @Override
    public List<StackRepoVO> repos(String stackName, String stackVersion) {
        Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);

        ImmutablePair<StackDTO, Set<ServiceDTO>> stackDTOSetImmutablePair = stackKeyMap.get(fullStackName);

        StackDTO stackDTO = stackDTOSetImmutablePair.left;
        List<RepoDTO> repoInfos = stackDTO.getRepos();

        List<StackRepoVO> stackRepoVOList = new ArrayList<>();
        for (RepoDTO repoDTO : repoInfos) {
            StackRepoVO stackRepoVO = RepoMapper.INSTANCE.DTO2VO(repoDTO, stackDTO);
            stackRepoVOList.add(stackRepoVO);
        }


        return stackRepoVOList;
    }

}
