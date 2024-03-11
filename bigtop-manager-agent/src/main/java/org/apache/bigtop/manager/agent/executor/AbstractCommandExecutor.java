package org.apache.bigtop.manager.agent.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.holder.SpringContextHolder;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.utils.Environments;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;

@Slf4j
public abstract class AbstractCommandExecutor implements CommandExecutor {

    protected CommandRequestMessage commandRequestMessage;

    protected CommandResponseMessage commandResponseMessage;

    @Override
    public void execute(CommandRequestMessage message) {
        commandRequestMessage = message;
        commandResponseMessage = new CommandResponseMessage();

        try {
            if (Environments.isDevMode()) {
                doExecuteOnDevMode();
            } else {
                doExecute();
            }
        } catch (Exception e) {
            commandResponseMessage.setCode(MessageConstants.FAIL_CODE);
            commandResponseMessage.setResult(e.getMessage());

            log.error("Run command failed, {}", message, e);
        }

        commandResponseMessage.setCommandMessageType(message.getCommandMessageType());
        commandResponseMessage.setMessageId(message.getMessageId());
        commandResponseMessage.setHostname(message.getHostname());
        commandResponseMessage.setTaskId(message.getTaskId());
        commandResponseMessage.setStageId(message.getStageId());
        commandResponseMessage.setJobId(message.getJobId());
        SpringContextHolder.getAgentWebSocket().sendMessage(commandResponseMessage);
    }

    protected void doExecuteOnDevMode() {
        commandResponseMessage.setCode(MessageConstants.SUCCESS_CODE);
        commandResponseMessage.setResult(ShellResult.success().getResult());
    }

    protected abstract void doExecute();
}
