package org.apache.bigtop.manager.agent.executor;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.entity.command.CommandMessageType;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.core.executor.Executor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentCommandExecutor extends AbstractCommandExecutor {

    @Resource
    private Executor stackExecutor;

    @Override
    public CommandMessageType getCommandMessageType() {
        return CommandMessageType.COMPONENT;
    }

    @Override
    protected void doExecuteOnDevMode() {
        doExecute();
    }

    @Override
    public void doExecute() {
        CommandPayload commandPayload = JsonUtils.readFromString(commandRequestMessage.getMessagePayload(), CommandPayload.class);
        log.info("[agent executeTask] taskEvent is: {}", commandRequestMessage);
        ShellResult shellResult = stackExecutor.execute(commandPayload);

        commandResponseMessage.setCode(shellResult.getExitCode());
        commandResponseMessage.setResult(shellResult.getResult());
    }
}
