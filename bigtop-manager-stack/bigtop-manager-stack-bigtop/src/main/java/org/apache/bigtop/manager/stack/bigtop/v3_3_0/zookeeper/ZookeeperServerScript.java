package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;
import org.apache.bigtop.manager.stack.spi.BaseParams;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    @Override
    public ShellResult install(BaseParams baseParams) {
        ZookeeperParams zookeeperParams = (ZookeeperParams) baseParams;
        List<String> packageList = zookeeperParams.getPackageList();

        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration(BaseParams baseParams) {
        return ZookeeperSetup.config(baseParams);
    }

    @Override
    public ShellResult start(BaseParams baseParams) {
        configuration(baseParams);
        ZookeeperParams zookeeperParams = (ZookeeperParams) baseParams;

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh start", zookeeperParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, zookeeperParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(BaseParams baseParams) {
        ZookeeperParams zookeeperParams = (ZookeeperParams) baseParams;
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh stop", zookeeperParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, zookeeperParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(BaseParams baseParams) {
        ZookeeperParams zookeeperParams = (ZookeeperParams) baseParams;
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh status", zookeeperParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, zookeeperParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
