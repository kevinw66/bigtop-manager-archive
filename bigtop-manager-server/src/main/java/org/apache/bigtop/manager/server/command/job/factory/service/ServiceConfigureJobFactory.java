package org.apache.bigtop.manager.server.command.job.factory.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceConfigureJobFactory extends AbstractServiceJobFactory {

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.CONFIGURE);
    }

    @Override
    public void createStagesAndTasks() {
        super.initAttrs();

        super.createCacheStage();
    }
}
