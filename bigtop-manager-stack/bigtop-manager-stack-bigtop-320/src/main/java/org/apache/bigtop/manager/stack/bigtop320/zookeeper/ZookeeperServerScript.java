package org.apache.bigtop.manager.stack.bigtop320.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;
import org.apache.bigtop.manager.stack.common.utils.template.PropertiesTemplate;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {


    @Override
    public void install() {
        log.info("install");
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("yum");

        builderParameters.add("install");

        builderParameters.add("zookeeper_3_2_0");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [install] output: {}", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void configuration() {
        log.info("configuration");
        log.info("{}", ZookeeperParams.zooCfg());
        PropertiesTemplate.writeProperties(ZookeeperParams.confDir() + "/zoo.cfg", ZookeeperParams.zooCfg());

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("JAVA_HOME", "/usr/local/java");
        modelMap.put("ZOOKEEPER_HOME", ZookeeperParams.zookeeperHome());
        modelMap.put("ZOO_LOG_DIR", ZookeeperParams.zookeeperEnv().get("logDir"));
        modelMap.put("ZOOPIDFILE", ZookeeperParams.zookeeperEnv().get("pidDir"));
        modelMap.put("securityEnabled", false);
        BaseTemplate.writeTemplateByContent(ZookeeperParams.confDir() + "/zookeeper-env.sh",
                modelMap, ZookeeperParams.zookeeperEnv().get("content").toString());
    }

    @Override
    public void start() {
        System.out.println("ZookeeperServerScript start");
        log.info("start");
        configuration();

        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        builderParameters.add(ZookeeperParams.zookeeperHome() + "/bin/zkServer.sh");
        builderParameters.add("start");
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
        log.info("stop");
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sh");

        builderParameters.add(ZookeeperParams.zookeeperHome() + "/bin/zkServer.sh");
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

        builderParameters.add(ZookeeperParams.zookeeperHome() + "/bin/zkServer.sh");
        builderParameters.add("status");
        builderParameters.add("ZOOKEEPER_SERVER");
        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[ZookeeperServerScript] [status] output: {}", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
