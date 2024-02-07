package org.apache.bigtop.manager.server.command.stage.factory.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentStopStageFactory extends AbstractComponentStageFactory {

    @Override
    public StageType getStageType() {
        return StageType.COMPONENT_STOP;
    }

    @Override
    protected Command getCommand() {
        return Command.STOP;
    }
}
