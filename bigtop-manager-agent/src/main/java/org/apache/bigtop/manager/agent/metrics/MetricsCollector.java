package org.apache.bigtop.manager.agent.metrics;

import com.fasterxml.jackson.databind.JsonNode;
import io.prometheus.metrics.core.metrics.Gauge;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.enums.AgentExceptionStatus;
import org.apache.bigtop.manager.agent.exception.AgentException;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class MetricsCollector {

    @Qualifier("diskGauge")
    @Resource
    private Gauge diskGauge;

    @Qualifier("memGauge")
    @Resource
    private Gauge memGauge;

    @Qualifier("cpuGauge")
    @Resource
    private Gauge cpuGauge;

    @Async
    @Scheduled(cron = "0/30 * *  * * ?")
    public void collect() {
        // refresh agent host monitoring data
        scrape();
    }

    private void scrape() {
        try {
            // DISK
            JsonNode agentMonitoring = AgentHostMonitoring.getHostInfo();
            Map<ArrayList<String>, Map<ArrayList<String>, Double>> diskGaugeMap = AgentHostMonitoring.getDiskGauge(agentMonitoring);
            diskGaugeMap.values().forEach(labelValues -> {
                for (Map.Entry<ArrayList<String>, Double> entry : labelValues.entrySet()) {
                    diskGauge.labelValues(entry.getKey().toArray(new String[0])).set(entry.getValue());
                }
            });

            // CPU
            Map<ArrayList<String>, Map<ArrayList<String>, Double>> cpuGaugeMap = AgentHostMonitoring.getCPUGauge(agentMonitoring);
            cpuGaugeMap.values().forEach(labelValues -> {
                for (Map.Entry<ArrayList<String>, Double> entry : labelValues.entrySet()) {
                    cpuGauge.labelValues(entry.getKey().toArray(new String[0])).set(entry.getValue());
                }
            });

            // MEM
            Map<ArrayList<String>, Map<ArrayList<String>, Double>> memGaugeMap = AgentHostMonitoring.getMEMGauge(agentMonitoring);
            memGaugeMap.values().forEach(labelValues -> {
                for (Map.Entry<ArrayList<String>, Double> entry : labelValues.entrySet()) {
                    memGauge.labelValues(entry.getKey().toArray(new String[0])).set(entry.getValue());
                }
            });
        } catch (UnknownHostException e) {
            throw new AgentException(AgentExceptionStatus.AGENT_MONITORING_ERROR);
        }
    }

}
