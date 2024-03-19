package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.shell.ShellResult;
import org.apache.bigtop.manager.spi.stack.Params;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;
import org.apache.bigtop.manager.spi.stack.Script;

import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    @Override
    public ShellResult install(Params params) {
        return PackageUtils.install(params.getPackageList());
    }

    @Override
    public ShellResult configure(Params params) {
        return ZookeeperSetup.config(params);
    }

    @Override
    public ShellResult start(Params params) {
        configure(params);
        ZookeeperParams zookeeperParams = (ZookeeperParams) params;

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh start", zookeeperParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, zookeeperParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(Params params) {
        ZookeeperParams zookeeperParams = (ZookeeperParams) params;
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh stop", zookeeperParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, zookeeperParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(Params params) {
        ZookeeperParams zookeeperParams = (ZookeeperParams) params;
        return LinuxOSUtils.checkProcess(zookeeperParams.getZookeeperPidFile());
    }

}
