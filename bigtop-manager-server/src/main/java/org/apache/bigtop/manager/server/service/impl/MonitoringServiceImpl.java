package org.apache.bigtop.manager.server.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.proxy.PrometheusProxy;
import org.apache.bigtop.manager.server.service.MonitoringService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MonitoringServiceImpl implements MonitoringService {

    @Resource
    PrometheusProxy prometheusProxy;

    @Override
    public JsonNode queryAgentsHealthyStatus() {
        return prometheusProxy.queryAgentsHealthyStatus();
    }
}
