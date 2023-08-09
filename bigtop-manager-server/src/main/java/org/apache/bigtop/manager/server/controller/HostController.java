package org.apache.bigtop.manager.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.stack.StackConfigUtils;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.request.HostRequest;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.ServiceConfig;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceConfigRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Host Controller")
@RestController
@RequestMapping("/hosts")
public class HostController {

    @Resource
    private HostService hostService;

    @Operation(summary = "list", description = "List hosts")
    @GetMapping
    public ResponseEntity<List<HostVO>> list() {
        return ResponseEntity.success(hostService.list());
    }

    @Operation(summary = "create", description = "Create a host")
    @PostMapping
    public ResponseEntity<HostVO> create(@RequestBody HostRequest hostRequest) {
        HostDTO hostDTO = HostMapper.INSTANCE.Request2DTO(hostRequest);
        return ResponseEntity.success(hostService.create(hostDTO));
    }

    @Operation(summary = "get", description = "Get a host")
    @GetMapping("/{id}")
    public ResponseEntity<HostVO> get(@PathVariable Long id) {
        return ResponseEntity.success(hostService.get(id));
    }

    @Operation(summary = "update", description = "Update a host")
    @PutMapping("/{id}")
    public ResponseEntity<HostVO> update(@PathVariable Long id, @RequestBody HostRequest hostRequest) {
        HostDTO hostDTO = HostMapper.INSTANCE.Request2DTO(hostRequest);
        return ResponseEntity.success(hostService.update(id, hostDTO));
    }

    @Operation(summary = "delete", description = "Delete a host")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.success(hostService.delete(id));
    }

    @Operation(summary = "cache", description = "distribute cache")
    @GetMapping("/cache")
    public void cache(@RequestParam Long clusterId) {
        hostService.cache(clusterId);
    }

    @Resource
    private ServiceConfigRepository serviceConfigRepository;
    @Resource
    private ClusterRepository clusterRepository;
    @Resource
    private ServiceRepository serviceRepository;

    @Operation(summary = "test", description = "test")
    @GetMapping("/test")
    public void test() throws JsonProcessingException {
        Map<String, Object> loadedConfig = StackConfigUtils.loadConfig(this.getClass().getClassLoader()
                .getResource("stacks/BIGTOP/3.2.0/services/ZOOKEEPER/configuration/zoo.cfg.yaml").getPath());

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setVersion(1);
        serviceConfig.setTypeName("zoo.cfg");
        serviceConfig.setConfigData(JsonUtils.OBJECTMAPPER.writeValueAsString(loadedConfig));
        clusterRepository.findByClusterName("c1").ifPresent(serviceConfig::setCluster);
        serviceRepository.findByServiceName("zookeeper").ifPresent(serviceConfig::setService);

        serviceConfigRepository.save(serviceConfig);

        Map<String, Object> loadedConfig2 = StackConfigUtils.loadConfig(this.getClass().getClassLoader()
                .getResource("stacks/BIGTOP/3.2.0/services/ZOOKEEPER/configuration/zookeeper-env.yaml").getPath());

        ServiceConfig serviceConfig2 = new ServiceConfig();
        serviceConfig2.setVersion(1);
        serviceConfig2.setTypeName("zookeeper-env");
        serviceConfig2.setConfigData(JsonUtils.OBJECTMAPPER.writeValueAsString(loadedConfig2));
        clusterRepository.findByClusterName("c1").ifPresent(serviceConfig2::setCluster);
        serviceRepository.findByServiceName("zookeeper").ifPresent(serviceConfig2::setService);

        serviceConfigRepository.save(serviceConfig2);
    }

}
