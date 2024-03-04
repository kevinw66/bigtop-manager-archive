package org.apache.bigtop.manager.agent;

import com.fasterxml.jackson.databind.JsonNode;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.exporter.servlet.jakarta.PrometheusMetricsServlet;
import org.apache.bigtop.manager.agent.enums.AgentExceptionStatus;
import org.apache.bigtop.manager.agent.exception.AgentException;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

@SpringBootApplication(scanBasePackages = {"org.apache.bigtop.manager.agent", "org.apache.bigtop.manager.common"})
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<PrometheusMetricsServlet> createPrometheusMetricsEndpoint() {
        return new ServletRegistrationBean<>(new PrometheusMetricsServlet(), "/metrics/*");
    }

    @Qualifier("diskGauge")
    @Bean
    public Gauge getDiskGauge() {
        try {
            JsonNode agentMonitoring = AgentHostMonitoring.getHostInfo();
            Map<ArrayList<String>, Map<ArrayList<String>, Double>> diskGaugeMap = AgentHostMonitoring.getDiskGauge(agentMonitoring);
            Gauge diskGauge = Gauge.builder()
                    .name("agent_host_monitoring_disk")
                    .help("BigTop Manager Agent Host Monitoring,Disk Monitoring")
                    .labelNames(diskGaugeMap.keySet().iterator().next().toArray(new String[0]))
                    .register();
            diskGaugeMap.values().forEach(labelValues -> {
                for (Map.Entry<ArrayList<String>, Double> entry : labelValues.entrySet()) {
                    diskGauge.labelValues(entry.getKey().toArray(new String[0])).set(entry.getValue());
                }
            });
            return diskGauge;
        } catch (UnknownHostException e) {
            throw new AgentException(AgentExceptionStatus.AGENT_MONITORING_ERROR);
        }
    }

    @Qualifier("cpuGauge")
    @Bean
    public Gauge getCPUGauge() {
        try {
            JsonNode agentMonitoring = AgentHostMonitoring.getHostInfo();
            Map<ArrayList<String>, Map<ArrayList<String>, Double>> cpuGaugeMap = AgentHostMonitoring.getCPUGauge(agentMonitoring);
            Gauge cpuGauge = Gauge.builder()
                    .name("agent_host_monitoring_cpu")
                    .help("BigTop Manager Agent Host Monitoring,CPU Monitoring")
                    .labelNames(cpuGaugeMap.keySet().iterator().next().toArray(new String[0]))
                    .register();
            cpuGaugeMap.values().forEach(labelValues -> {
                for (Map.Entry<ArrayList<String>, Double> entry : labelValues.entrySet()) {
                    cpuGauge.labelValues(entry.getKey().toArray(new String[0])).set(entry.getValue());
                }
            });
            return cpuGauge;
        } catch (UnknownHostException e) {
            throw new AgentException(AgentExceptionStatus.AGENT_MONITORING_ERROR);
        }
    }

    @Qualifier("memGauge")
    @Bean
    public Gauge getMEMGauge() {
        try {
            JsonNode agentMonitoring = AgentHostMonitoring.getHostInfo();
            Map<ArrayList<String>, Map<ArrayList<String>, Double>> memGaugeMap = AgentHostMonitoring.getMEMGauge(agentMonitoring);
            Gauge memGauge = Gauge.builder()
                    .name("agent_host_monitoring_mem")
                    .help("BigTop Manager Agent Host Monitoring,MEM Monitoring")
                    .labelNames(memGaugeMap.keySet().iterator().next().toArray(new String[0]))
                    .register();
            memGaugeMap.values().forEach(labelValues -> {
                for (Map.Entry<ArrayList<String>, Double> entry : labelValues.entrySet()) {
                    memGauge.labelValues(entry.getKey().toArray(new String[0])).set(entry.getValue());
                }
            });
            return memGauge;
        } catch (UnknownHostException e) {
            throw new AgentException(AgentExceptionStatus.AGENT_MONITORING_ERROR);
        }
    }
}
