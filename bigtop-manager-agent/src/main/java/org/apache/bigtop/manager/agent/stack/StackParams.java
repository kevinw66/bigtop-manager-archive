package org.apache.bigtop.manager.agent.stack;

import lombok.Data;
import org.apache.bigtop.manager.agent.configuration.StackConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Obtain Params from BigTop Manager Server
 */
@Data
@Component
public class StackParams {

    @Resource
    private StackConfiguration stackConfiguration;

    private String stackName = "BIGTOP";
    private String stackRoot = "/usr/bigtop";

    private String stackVersion = "3.2.0";

    public String getStackParentCacheDir() {
        return stackConfiguration.getCacheDir() + "/stacks";
    }

    public String getStackCacheDir() {
        return getStackParentCacheDir() + "/" + stackName + "/" + stackVersion;
    }
}
