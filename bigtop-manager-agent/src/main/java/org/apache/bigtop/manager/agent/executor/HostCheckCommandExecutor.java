package org.apache.bigtop.manager.agent.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.entity.command.CommandMessageType;
import org.apache.bigtop.manager.common.utils.os.TimeSyncDetection;
import org.apache.bigtop.manager.common.shell.ShellResult;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostCheckCommandExecutor extends AbstractCommandExecutor {

    @Override
    public CommandMessageType getCommandMessageType() {
        return CommandMessageType.HOST_CHECK;
    }

    @Override
    public void doExecute() {
        ShellResult shellResult = runChecks(List.of(this::checkTimeSync));
        commandResponseMessage.setCode(shellResult.getExitCode());
        commandResponseMessage.setResult(shellResult.getResult());
    }

    private ShellResult runChecks(List<Supplier<ShellResult>> suppliers) {
        ShellResult shellResult = ShellResult.success();
        for (Supplier<ShellResult> supplier : suppliers) {
            shellResult = supplier.get();
            if (shellResult.getExitCode() != MessageConstants.SUCCESS_CODE) {
                return shellResult;
            }
        }

        return shellResult;
    }

    private ShellResult checkTimeSync() {
        ShellResult shellResult = TimeSyncDetection.checkTimeSync();
        log.info("Time sync check result: {}", shellResult.getResult());

        return shellResult;
    }
}
