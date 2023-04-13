package org.apache.bigtop.manager.agent.stack.service.zookeeper;

import org.apache.bigtop.manager.agent.stack.ServiceEnv;
import org.apache.bigtop.manager.agent.stack.StackParams;
import org.apache.bigtop.manager.agent.utils.template.BaseTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZookeeperServiceEnv implements ServiceEnv {

    @Resource
    private BaseTemplate baseTemplate;

    @Resource
    private StackParams stackParams;

    @Resource
    private ZookeeperParams zookeeperParams;

    @Override
    public void initEnv() {
        String path = zookeeperParams.getScriptDir() + "/service_env.sh";

        Map<String, Object> config = new HashMap<>();

        config.put("JAVA_HOME", "/usr/local/java");
        config.put("ZOOKEEPER_HOME", zookeeperParams.getZookeeperHome());
        config.put("ZOOKEEPER_CONF_DIR", zookeeperParams.getConfDir());
        config.put("STACK_ROOT", stackParams.getStackRoot());
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", config);

        baseTemplate.writeTemplate(path, modelMap, "env");
    }
}
