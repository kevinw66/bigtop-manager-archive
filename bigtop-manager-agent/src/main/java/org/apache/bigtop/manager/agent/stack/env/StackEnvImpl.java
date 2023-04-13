package org.apache.bigtop.manager.agent.stack.env;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.configuration.StackConfiguration;
import org.apache.bigtop.manager.agent.stack.StackEnv;
import org.apache.bigtop.manager.agent.stack.StackParams;
import org.apache.bigtop.manager.agent.utils.template.BaseTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StackEnvImpl implements StackEnv {
    @Resource
    private StackConfiguration stackConfiguration;

    @Resource
    private StackParams stackParams;

    @Resource
    private BaseTemplate baseTemplate;

    /**
     * init stack env
     */
    @Override
    public void initEnv() {
        String path = stackParams.getStackCacheDir() + "/" + stackConfiguration.getEnvFile();

        Map<String, Object> config = new HashMap<>();

        config.put("JAVA_HOME", "/usr/local/java");
        config.put("a", "test");
        config.put("STACK_ROOT", stackParams.getStackRoot());
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", config);

        baseTemplate.writeTemplate(path, modelMap, "env");
    }
}
