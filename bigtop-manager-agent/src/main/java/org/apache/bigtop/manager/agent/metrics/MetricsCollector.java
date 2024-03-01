package org.apache.bigtop.manager.agent.metrics;

import com.fasterxml.jackson.databind.JsonNode;
import io.prometheus.metrics.core.metrics.Gauge;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class MetricsCollector {

    @Resource
    private Gauge gauge;

    @Async
    @Scheduled(cron = "0/30 * *  * * ?")
    public void collect() {
        // refresh agent host monitoring data
        scrape();
    }

    private void scrape() {
        try {
            JsonNode hostInfo = AgentHostMonitoring.getHostInfo();
            ArrayList<String> values = new ArrayList<>();
            Iterator<String> fieldNames = hostInfo.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                values.add(hostInfo.get(field).asText());
            }

            gauge.labelValues(values.toArray(new String[0]));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
