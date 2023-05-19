package org.apache.bigtop.manager.server.service.impl;

import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.service.HostService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class HostServiceImpl implements HostService {

    @Resource
    private HostRepository hostRepository;

    @Override
    public List<HostVO> list() {
        List<HostVO> hostVOList = new ArrayList<>();
        hostRepository.findAll().forEach(host -> {
            HostVO hostVO = new HostVO();
            BeanUtils.copyProperties(host, hostVO);
            hostVOList.add(hostVO);
        });

        return hostVOList;
    }

    @Override
    public HostVO create(HostDTO hostDTO) {
        Host host = new Host();
        BeanUtils.copyProperties(hostDTO, host);
        hostRepository.save(host);

        HostVO hostVO = new HostVO();
        BeanUtils.copyProperties(host, hostVO);
        return hostVO;
    }

    @Override
    public HostVO get(Long id) {
        HostVO hostVO = new HostVO();
        Host host = hostRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.HOST_NOT_FOUND));
        BeanUtils.copyProperties(host, hostVO);
        return hostVO;
    }

    @Override
    public HostVO update(Long id, HostDTO hostDTO) {
        Host host = new Host();
        BeanUtils.copyProperties(hostDTO, host);
        host.setId(id);
        hostRepository.save(host);

        HostVO hostVO = new HostVO();
        BeanUtils.copyProperties(host, hostVO);
        return hostVO;
    }

    @Override
    public Boolean delete(Long id) {
        hostRepository.deleteById(id);
        return true;
    }
}
