package org.apache.bigtop.manager.server.service.impl;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.pojo.stack.RepoInfo;
import org.apache.bigtop.manager.common.pojo.stack.ServiceInfo;
import org.apache.bigtop.manager.common.pojo.stack.StackInfo;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.service.StackService;
import org.apache.bigtop.manager.server.stack.StackInitialization;
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

    @Resource
    private StackInitialization stackInitialization;

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
        Map<String, ImmutablePair<StackInfo, Set<ServiceInfo>>> stackKeyMap = stackInitialization.getStackKeyMap();

        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);

        ImmutablePair<StackInfo, Set<ServiceInfo>> stackInfoSetImmutablePair = stackKeyMap.get(fullStackName);

        List<ServiceVersionVO> serviceVersionVOList = new ArrayList<>();
        for (ServiceInfo serviceInfo : stackInfoSetImmutablePair.right) {
            ServiceVersionVO serviceVersionVO = ServiceMapper.INSTANCE.POJO2VO(serviceInfo);
            serviceVersionVOList.add(serviceVersionVO);
        }

        return serviceVersionVOList;
    }

    @Override
    public List<StackRepoVO> repos(String stackName, String stackVersion) {
        Map<String, ImmutablePair<StackInfo, Set<ServiceInfo>>> stackKeyMap = stackInitialization.getStackKeyMap();

        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);

        ImmutablePair<StackInfo, Set<ServiceInfo>> stackInfoSetImmutablePair = stackKeyMap.get(fullStackName);

        StackInfo stackInfo = stackInfoSetImmutablePair.left;
        List<RepoInfo> repoInfos = stackInfo.getRepos();

        List<StackRepoVO> stackRepoVOList = new ArrayList<>();
        for (RepoInfo repoInfo : repoInfos) {
            StackRepoVO stackRepoVO = RepoMapper.INSTANCE.POJO2VO(repoInfo, stackInfo);
            stackRepoVOList.add(stackRepoVO);
        }


        return stackRepoVOList;
    }

}
