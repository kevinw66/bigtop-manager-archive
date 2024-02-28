package org.apache.bigtop.manager.stack.nop.v1_0_0.kafka;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.spi.stack.Params;
import org.apache.bigtop.manager.spi.stack.Script;

@Slf4j
@AutoService(Script.class)
public class KafkaBrokerScript implements Script {

    @Override
    public ShellResult install(Params params) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult configure(Params params) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult start(Params params) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult stop(Params params) {
        return DefaultShellResult.success();
    }

    @Override
    public ShellResult status(Params params) {
        return DefaultShellResult.success();
    }

    public ShellResult test(Params params) {
        return DefaultShellResult.success();
    }

}
