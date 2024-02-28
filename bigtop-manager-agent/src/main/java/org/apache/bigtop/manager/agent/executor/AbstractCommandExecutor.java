package org.apache.bigtop.manager.agent.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.holder.SpringContextHolder;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.utils.Environments;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;

@Slf4j
public abstract class AbstractCommandExecutor implements CommandExecutor {

    @Override
    public void execute(CommandRequestMessage message) {
        CommandResponseMessage commandResponseMessage = new CommandResponseMessage();

        try {
            if (!Environments.isDevMode()) {
                commandResponseMessage = doExecute(message);
            } else {
                commandResponseMessage.setCode(MessageConstants.SUCCESS_CODE);
                commandResponseMessage.setResult(DefaultShellResult.success().getResult());
            }
        } catch (Exception e) {
            commandResponseMessage.setCode(MessageConstants.DEFAULT_FAIL_CODE);
            commandResponseMessage.setResult(e.getMessage());

            log.error("Run command failed, {}", message, e);
        }

        commandResponseMessage.setMessageType(message.getMessageType());
        commandResponseMessage.setMessageId(message.getMessageId());
        commandResponseMessage.setHostname(message.getHostname());
        commandResponseMessage.setTaskId(message.getTaskId());
        commandResponseMessage.setStageId(message.getStageId());
        commandResponseMessage.setJobId(message.getJobId());
        SpringContextHolder.getAgentWebSocket().sendMessage(commandResponseMessage);
    }

    protected abstract CommandResponseMessage doExecute(CommandRequestMessage message);
}
