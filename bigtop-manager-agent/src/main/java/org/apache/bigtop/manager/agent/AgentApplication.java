package org.apache.bigtop.manager.agent;

import com.fasterxml.jackson.databind.JsonNode;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.exporter.servlet.jakarta.PrometheusMetricsServlet;
import org.apache.bigtop.manager.agent.enums.AgentExceptionStatus;
import org.apache.bigtop.manager.agent.exception.AgentException;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

@SpringBootApplication(scanBasePackages = {"org.apache.bigtop.manager.agent", "org.apache.bigtop.manager.common"})
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<PrometheusMetricsServlet> createPrometheusMetricsEndpoint() {
        return new ServletRegistrationBean<>(new PrometheusMetricsServlet(), "/metrics/*");
    }

    @Bean
    public Gauge getGauge(){
        try {
            JsonNode hostInfo = AgentHostMonitoring.getHostInfo();
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            Iterator<String> fieldNames = hostInfo.fieldNames();
            while (fieldNames.hasNext()){
                String field = fieldNames.next();
                keys.add(field);
                values.add(hostInfo.get(field).asText());
            }
            Gauge gauge = Gauge.builder().name("AgentHostMonitoring").labelNames(keys.toArray(new String[0])).register();
            gauge.initLabelValues(values.toArray(new String[0]));
            //info.setLabelValues();
            return gauge;
        } catch (UnknownHostException e) {
            throw new AgentException(AgentExceptionStatus.AGENT_MONITORING_ERROR);
        }
    }
}
