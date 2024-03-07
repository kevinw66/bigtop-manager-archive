package org.apache.bigtop.manager.server.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface MonitoringService {

    JsonNode queryAgentsHealthyStatus();
}
