package org.apache.bigtop.manager.agent.stack.service.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.stack.Script;
import org.apache.bigtop.manager.agent.utils.template.BaseTemplate;
import org.apache.bigtop.manager.agent.utils.template.PropertiesTemplate;
import org.apache.bigtop.manager.common.shell.ShellExecutor;
import org.apache.bigtop.manager.common.shell.ShellResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(value = "ZookeeperServerScript")
public class ZookeeperServerScript implements Script {

    @Resource
    private ZookeeperParams params;

    @Resource
    private PropertiesTemplate propertiesTemplate;

    @Resource
    private BaseTemplate baseTemplate;

    @Resource
    private ZookeeperServiceEnv zookeeperServiceEnv;

    @Override
    public void install() {
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        String commandString = params.getScriptDir() + "/install.sh";
        builderParameters.add(commandString);
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [install] output: {}", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void configuration() {
        zookeeperServiceEnv.initEnv();
        log.info("{}", params.getZooCfg());
        propertiesTemplate.writeProperties(params.getConfDir() + "/zoo.cfg", params.getZooCfg());

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("JAVA_HOME", "/usr/local/java");
        modelMap.put("ZOOKEEPER_HOME", params.getZookeeperHome());
        modelMap.put("ZOO_LOG_DIR", params.getZookeeperEnv().get("logDir"));
        modelMap.put("ZOOPIDFILE", params.getZookeeperEnv().get("pidDir"));
        modelMap.put("securityEnabled", false);
//        modelMap.put("SERVER_JVMFLAGS", "");
        baseTemplate.writeTemplateByContent(params.getConfDir() + "/zookeeper-env.sh",
                modelMap, params.getZookeeperEnv().get("content").toString());
    }

    @Override
    public void start() {
        zookeeperServiceEnv.initEnv();
        configuration();

        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        String commandString = params.getScriptDir() + "/service.sh";
        builderParameters.add(commandString);
        builderParameters.add("start");
        builderParameters.add("ZOOKEEPER_SERVER");
        log.info("{}", builderParameters);
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);

            log.info("[ZookeeperServerScript] [start] output: {}", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        String commandString = params.getScriptDir() + "/service.sh";
        builderParameters.add(commandString);
        builderParameters.add("stop");
        builderParameters.add("ZOOKEEPER_SERVER");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [stop] output: {}", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void status() {
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        String commandString = params.getScriptDir() + "/service.sh";
        builderParameters.add(commandString);
        builderParameters.add("status");
        builderParameters.add("ZOOKEEPER_SERVER");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [status] output: {}", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
