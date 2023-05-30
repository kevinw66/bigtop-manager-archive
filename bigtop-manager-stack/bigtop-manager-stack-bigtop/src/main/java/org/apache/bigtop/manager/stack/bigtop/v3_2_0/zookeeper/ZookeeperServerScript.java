package org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.PropertiesUtils;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    //TODO Need to adapt to multi-architecture systems
    @Override
    public void install() {
        log.info("ZookeeperServerScript install");
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("yum");

        builderParameters.add("install");

        builderParameters.add("-y");

        builderParameters.add("zookeeper_3_2_0");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [install] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public void configuration() {
        log.info("ZookeeperServerScript configuration");

        log.info("{}", ZookeeperParams.zooCfg());
        PropertiesUtils.writeProperties(ZookeeperParams.confDir() + "/zoo.cfg", ZookeeperParams.zooCfg());

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("JAVA_HOME", "/usr/local/java");
        modelMap.put("ZOOKEEPER_HOME", ZookeeperParams.zookeeperHome());
        modelMap.put("ZOO_LOG_DIR", ZookeeperParams.zookeeperEnv().get("logDir"));
        modelMap.put("ZOOPIDFILE", ZookeeperParams.zookeeperEnv().get("pidDir"));
        modelMap.put("securityEnabled", false);

        log.info("modelMap: {}", modelMap);
        log.info("content: {}", ZookeeperParams.zookeeperEnv().get("content"));
        BaseTemplate.writeTemplateByContent(ZookeeperParams.confDir() + "/zookeeper-env.sh",
                modelMap, ZookeeperParams.zookeeperEnv().get("content").toString());
    }

    @Override
    public void start() {
        configuration();
        log.info("ZookeeperServerScript start");

        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        builderParameters.add(ZookeeperParams.zookeeperHome() + "/bin/zkServer.sh");
        builderParameters.add("start");
        log.info("{}", builderParameters);
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);

            log.info("[ZookeeperServerScript] [start] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public void stop() {
        log.info("ZookeeperServerScript stop");
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        builderParameters.add(ZookeeperParams.zookeeperHome() + "/bin/zkServer.sh");
        builderParameters.add("stop");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [stop] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public void status() {
        log.info("ZookeeperServerScript status");
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        builderParameters.add(ZookeeperParams.zookeeperHome() + "/bin/zkServer.sh");
        builderParameters.add("status");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [status] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
