package org.apache.bigtop.manager.server.command.job.factory.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.command.stage.factory.StageContext;
import org.apache.bigtop.manager.server.command.stage.factory.StageFactories;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostAddJobFactory extends AbstractHostJobFactory {

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.HOST, Command.INSTALL);
    }

    public void createStagesAndTasks() {
        StageContext stageContext = StageContext.fromPayload(JsonUtils.writeAsString(jobContext.getCommandDTO()));
        stages.add(StageFactories.getStageFactory(StageType.HOST_CHECK).createStage(stageContext));
        stages.add(StageFactories.getStageFactory(StageType.HOST_CACHE).createStage(stageContext));
    }
}
