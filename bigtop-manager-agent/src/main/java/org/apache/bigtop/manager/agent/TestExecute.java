package org.apache.bigtop.manager.agent;

import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.stack.core.ExecutorImpl;

//Test class
public class TestExecute {
    public static void main(String[] args) {
        CommandMessage commandMessage = new CommandMessage();
        commandMessage.setScriptId("org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper.ZookeeperServerScript");

        commandMessage.setVersion("3.2.0");
        commandMessage.setStack("bigtop");
        commandMessage.setService("zookeeper");

        commandMessage.setCacheDir("/var/lib/bigtop-manager-agent/cache");


        CommandMessage commandMessage1 = YamlUtils.readYaml("/var/lib/bigtop-manager-agent/cache/stacks/BIGTOP/3.2.0/services/ZOOKEEPER/metainfo.yaml", CommandMessage.class);
        commandMessage.setOsSpecifics(commandMessage1.getOsSpecifics());
        ExecutorImpl execute = new ExecutorImpl();


        commandMessage.setCommand("configuration");
        execute.execute(commandMessage);

        commandMessage.setCommand("status");
        execute.execute(commandMessage);


    }
}
