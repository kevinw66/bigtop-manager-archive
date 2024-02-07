package org.apache.bigtop.manager.server.command.job.runner.host;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.command.job.runner.AbstractJobRunner;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.HostCommandDTO;
import org.apache.bigtop.manager.server.service.HostService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostAddJobRunner extends AbstractJobRunner {

    @Resource
    private HostService hostService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.HOST, Command.INSTALL);
    }

    @Override
    public void onSuccess() {
        super.onSuccess();

        CommandDTO commandDTO = JsonUtils.readFromString(job.getPayload(), CommandDTO.class);
        List<HostCommandDTO> hostCommands = commandDTO.getHostCommands();

        List<String> hostnames = hostCommands.stream().map(HostCommandDTO::getHostname).toList();
        hostService.batchSave(job.getCluster().getId(), hostnames);
    }
}
