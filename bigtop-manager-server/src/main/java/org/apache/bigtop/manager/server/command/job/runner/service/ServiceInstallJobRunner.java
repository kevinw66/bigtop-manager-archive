package org.apache.bigtop.manager.server.command.job.runner.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.command.job.runner.AbstractJobRunner;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceInstallJobRunner extends AbstractJobRunner {

    @Resource
    private ServiceService serviceService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.INSTALL);
    }

    @Override
    public void beforeRun() {
        super.beforeRun();

        serviceService.saveByCommand(getCommandDTO());
    }
}
