package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    @Override
    public ShellResult install(CommandPayload commandMessage) {
        List<String> packageList = ZookeeperParams.getPackageList(commandMessage);

        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration(CommandPayload commandMessage) {
        return ZookeeperSetup.config(commandMessage);
    }

    @Override
    public ShellResult start(CommandPayload commandMessage) {
        configuration(commandMessage);

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh start", ZookeeperParams.serviceHome(commandMessage));
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(CommandPayload commandMessage) {
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh stop", ZookeeperParams.serviceHome(commandMessage));
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(CommandPayload commandMessage) {
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh status", ZookeeperParams.serviceHome(commandMessage));
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
