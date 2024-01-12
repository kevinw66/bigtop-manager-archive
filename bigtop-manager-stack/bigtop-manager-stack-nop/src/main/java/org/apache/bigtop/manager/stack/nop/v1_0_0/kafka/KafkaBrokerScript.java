package org.apache.bigtop.manager.stack.nop.v1_0_0.kafka;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.spi.BaseParams;
import org.apache.bigtop.manager.stack.spi.Script;

@Slf4j
@AutoService(Script.class)
public class KafkaBrokerScript implements Script {

    @Override
    public ShellResult install(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult configure(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult start(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult stop(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult status(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

    public ShellResult test(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

}
