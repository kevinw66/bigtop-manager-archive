package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.spi.ClientScript;
import org.apache.bigtop.manager.stack.spi.Script;

import java.util.List;

@Slf4j
@AutoService(Script.class)
public class ZookeeperClientScript implements ClientScript {

    @Override
    public ShellResult install(CommandPayload commandMessage) {
        List<String> packageList = ZookeeperParams.getPackageList(commandMessage);

        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration(CommandPayload commandMessage) {
        return ZookeeperSetup.config(commandMessage);
    }

}
