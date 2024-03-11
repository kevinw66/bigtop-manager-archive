package org.apache.bigtop.manager.agent.metrics;

import io.micrometer.core.instrument.MultiGauge;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class MetricsCollector {

    @Qualifier("diskMultiGauge")
    @Resource
    private MultiGauge diskMultiGauge;

    @Qualifier("memMultiGauge")
    @Resource
    private MultiGauge memMultiGauge;

    @Qualifier("cpuMultiGauge")
    @Resource
    private MultiGauge cpuMultiGauge;

    @Async
    @Scheduled(cron = "*/10 * *  * * ?")
    public void collect() {
        // refresh agent host monitoring data
        scrape();
    }

    private void scrape() {
        AgentHostMonitoring.diskMultiGaugeUpdateData(diskMultiGauge);
        AgentHostMonitoring.memMultiGaugeUpdateData(memMultiGauge);
        AgentHostMonitoring.cpuMultiGaugeUpdateData(cpuMultiGauge);
    }

}
