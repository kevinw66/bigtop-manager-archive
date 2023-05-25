package org.apache.bigtop.manager.agent;

import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.stack.core.ExecutorImpl;

public class TestExecute {
    public static void main(String[] args) {
        CommandMessage commandMessage = new CommandMessage();
        commandMessage.setScriptId("org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper.ZookeeperServerScript");

        commandMessage.setVersion("3.2.0");
        commandMessage.setStack("bigtop");
        commandMessage.setService("zookeeper");

        commandMessage.setCacheDir("/var/lib/bigtop-manager-agent/cache");

        commandMessage.setCommand("install");
        ExecutorImpl execute = new ExecutorImpl();
        execute.execute(commandMessage);

        commandMessage.setCommand("start");
        execute.execute(commandMessage);

        commandMessage.setCommand("status");
        execute.execute(commandMessage);

        commandMessage.setCommand("stop");
        execute.execute(commandMessage);

    }
}
