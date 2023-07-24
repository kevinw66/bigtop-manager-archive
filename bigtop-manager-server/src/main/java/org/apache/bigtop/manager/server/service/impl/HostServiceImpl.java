package org.apache.bigtop.manager.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.service.HostService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HostServiceImpl implements HostService {

    private final HostRepository hostRepository;

    @Override
    public List<HostVO> list() {
        List<HostVO> hostVOList = new ArrayList<>();
        hostRepository.findAll().forEach(host -> {
            HostVO hostVO = HostMapper.INSTANCE.Entity2VO(host);
            hostVOList.add(hostVO);
        });

        return hostVOList;
    }

    @Override
    public HostVO create(HostDTO hostDTO) {
        Host host = HostMapper.INSTANCE.DTO2Entity(hostDTO);
        hostRepository.save(host);

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public HostVO get(Long id) {
        Host host = hostRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.HOST_NOT_FOUND));

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public HostVO update(Long id, HostDTO hostDTO) {
        Host host = HostMapper.INSTANCE.DTO2Entity(hostDTO);
        host.setId(id);
        hostRepository.save(host);

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public Boolean delete(Long id) {
        hostRepository.deleteById(id);
        return true;
    }
}
