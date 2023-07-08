package org.apache.bigtop.manager.server.service;

public interface ComponentService {

    void handleComponent(String clusterName, String componentName, String action);
}
