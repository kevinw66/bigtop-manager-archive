package org.apache.bigtop.manager.agent.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO need to support script other than shell
 */
@Slf4j
@Component
public class DefaultCommandRunner implements CommandRunner {

    @Override
    public void run(CommandMessage commandMessage) {
        String stack = commandMessage.getStack().toUpperCase();
        String version = commandMessage.getVersion();
        String service = commandMessage.getService().toUpperCase();

        String stackDir = MessageFormat.format("/stacks/{0}/{1}", stack, version);
        String scriptDir = stackDir + MessageFormat.format("/services/{2}/scripts", service);

        configureStackEnvironment(stackDir);

        runCommand(scriptDir, commandMessage.getBeforeCommand());
        runCommand(scriptDir, commandMessage.getCommand());
        runCommand(scriptDir, commandMessage.getAfterCommand());
    }

    private void configureStackEnvironment(String stackDir) {
        runNativeCommand(stackDir + "stack-env.sh");
    }

    private void runCommand(String scriptDir, String command) {
        runNativeCommand(scriptDir + "/" + command + ".sh");
    }

    private void runNativeCommand(String resourceLocation) {
        try {
            String filePath = ResourceUtils.getFile(resourceLocation).getAbsolutePath();

            List<String> builderParameters = new ArrayList<>();
            builderParameters.add("sh");
            builderParameters.add(filePath);
            log.info("Running command: {}", StringUtils.join(builderParameters));
            ShellResult result = ShellExecutor.execCommand(builderParameters);
            log.info("Command execution result: {}", result);
        } catch (FileNotFoundException e) {
            log.warn("File not exist: {}, considered no need to run", resourceLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
