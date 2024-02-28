package org.apache.bigtop.manager.agent.executor;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
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
    public MessageType getMessageType() {
        return MessageType.COMPONENT;
    }

    @Override
    public CommandResponseMessage doExecute(CommandRequestMessage message) {
        CommandResponseMessage commandResponseMessage = new CommandResponseMessage();
        CommandPayload commandPayload = JsonUtils.readFromString(message.getMessagePayload(), CommandPayload.class);
        log.info("[agent executeTask] taskEvent is: {}", message);
        ShellResult shellResult = stackExecutor.execute(commandPayload);

        commandResponseMessage.setCode(shellResult.getExitCode());
        commandResponseMessage.setResult(shellResult.getResult());
        return commandResponseMessage;
    }
}
