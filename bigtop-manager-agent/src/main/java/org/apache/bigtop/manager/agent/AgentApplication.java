package org.apache.bigtop.manager.agent;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"org.apache.bigtop.manager.agent", "org.apache.bigtop.manager.common"})
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

    @Qualifier("diskMultiGauge")
    @Bean
    public MultiGauge diskMultiGauge(MeterRegistry meterRegistry) {
        return AgentHostMonitoring.newDiskMultiGauge(meterRegistry);
    }

    @Qualifier("cpuMultiGauge")
    @Bean
    public MultiGauge cpuMultiGauge(MeterRegistry meterRegistry) {
        return AgentHostMonitoring.newCPUMultiGauge(meterRegistry);
    }

    @Qualifier("memMultiGauge")
    @Bean
    public MultiGauge memMultiGauge(MeterRegistry meterRegistry) {
        return AgentHostMonitoring.newMemMultiGauge(meterRegistry);
    }


}
