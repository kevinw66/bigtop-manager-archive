package org.apache.bigtop.manager.agent.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.message.entity.payload.CacheMessagePayload;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.text.MessageFormat;

import static org.apache.bigtop.manager.common.constants.CacheFiles.*;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CacheDistributeCommandExecutor extends AbstractCommandExecutor {

    @Override
    public MessageType getMessageType() {
        return MessageType.CACHE_DISTRIBUTE;
    }

    @Override
    public CommandResponseMessage doExecute(CommandRequestMessage message) {
        CacheMessagePayload cacheMessagePayload = JsonUtils.readFromString(message.getMessagePayload(), CacheMessagePayload.class);
        log.info("[agent executeTask] taskEvent is: {}", message);
        String cacheDir = Constants.STACK_CACHE_DIR;

        LinuxFileUtils.createDirectories(cacheDir, "root", "root", "rwxr-xr-x", false);

        JsonUtils.writeToFile(cacheDir + SETTINGS_INFO, cacheMessagePayload.getSettings());
        JsonUtils.writeToFile(cacheDir + CONFIGURATIONS_INFO, cacheMessagePayload.getConfigurations());
        JsonUtils.writeToFile(cacheDir + HOSTS_INFO, cacheMessagePayload.getClusterHostInfo());
        JsonUtils.writeToFile(cacheDir + USERS_INFO, cacheMessagePayload.getUserInfo());
        JsonUtils.writeToFile(cacheDir + COMPONENTS_INFO, cacheMessagePayload.getComponentInfo());
        JsonUtils.writeToFile(cacheDir + REPOS_INFO, cacheMessagePayload.getRepoInfo());
        JsonUtils.writeToFile(cacheDir + CLUSTER_INFO, cacheMessagePayload.getClusterInfo());

        CommandResponseMessage commandResponseMessage = new CommandResponseMessage();
        commandResponseMessage.setCode(MessageConstants.SUCCESS_CODE);
        commandResponseMessage.setResult(MessageFormat.format("Host [{0}] cached successful!!!", message.getHostname()));

        return commandResponseMessage;
    }
}
