package org.apache.bigtop.manager.stack.bigtop.v3_3_0.hdfs;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.spi.stack.Params;
import org.apache.bigtop.manager.spi.stack.Script;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;

import java.text.MessageFormat;


@Slf4j
@AutoService(Script.class)
public class NameNodeScript implements Script {

    @Override
    public ShellResult install(Params params) {
        return PackageUtils.install(params.getPackageList());
    }

    @Override
    public ShellResult configure(Params params) {
        return HdfsSetup.config(params, "namenode");
    }

    @Override
    public ShellResult start(Params params) {
        configure(params);
        HdfsParams hdfsParams = (HdfsParams) params;

        HdfsSetup.formatNameNode(hdfsParams);

        String cmd = MessageFormat.format("{0} --daemon start namenode", hdfsParams.hdfsExec());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, hdfsParams.user());
        } catch (Exception e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(Params params) {
        HdfsParams hdfsParams = (HdfsParams) params;
        String cmd = MessageFormat.format("{0} --daemon stop namenode", hdfsParams.hdfsExec());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, hdfsParams.user());
        } catch (Exception e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(Params params) {
        HdfsParams hdfsParams = (HdfsParams) params;
        return LinuxOSUtils.checkProcess(hdfsParams.getNameNodePidFile());
    }


    public ShellResult rebalanceHdfs(Params params) {
        HdfsParams hdfsParams = (HdfsParams) params;
        String cmd = MessageFormat.format("{0} balancer", hdfsParams.hdfsExec());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, hdfsParams.user());
        } catch (Exception e) {
            throw new StackException(e);
        }
    }

    public ShellResult printTopology(Params params) {
        HdfsParams hdfsParams = (HdfsParams) params;
        String cmd = MessageFormat.format("{0} dfsadmin -printTopology", hdfsParams.hdfsExec());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, hdfsParams.user());
        } catch (Exception e) {
            throw new StackException(e);
        }
    }

}
