package org.apache.bigtop.manager.agent.mpack;

import org.apache.bigtop.manager.spi.mpack.Script;
import org.apache.bigtop.manager.spi.plugin.SPIFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Execute when init cluster
 * Then get all Script Object for all services
 */
@Component
public class ServiceScriptManager {

    private Map<String, Script> scriptMap;

    public void initScripts() {
        SPIFactory<Script> spiFactory = new SPIFactory<>(Script.class);
        scriptMap = spiFactory.getSPIMap();
    }

    public Map<String, Script> getScriptMap() {
        return scriptMap;
    }

    public Script getScript(String id) {
        return scriptMap.get(id);
    }


}