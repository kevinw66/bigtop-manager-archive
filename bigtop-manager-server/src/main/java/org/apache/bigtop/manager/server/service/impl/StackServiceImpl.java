package org.apache.bigtop.manager.server.service.impl;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.service.StackService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

}
