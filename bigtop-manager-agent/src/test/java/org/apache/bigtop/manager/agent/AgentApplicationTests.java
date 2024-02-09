package org.apache.bigtop.manager.agent;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.bigtop.manager.agent.hostmonitoring.AgentHostMonitoring;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.UnknownHostException;
import java.text.DecimalFormat;

@SpringBootTest
public class AgentApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getHostAgentInfo() throws UnknownHostException {
        JsonNode hostInfo = AgentHostMonitoring.getHostInfo();
        System.out.println(hostInfo.toPrettyString());
    }

    @Test
    void testNum() {
        System.out.println(new DecimalFormat("#.00").format(123.2344));
    }

}
