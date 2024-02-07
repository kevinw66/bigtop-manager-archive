package org.apache.bigtop.manager.server.command.stage.runner.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.server.command.stage.runner.AbstractStageRunner;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostCheckStageRunner extends AbstractStageRunner {

    public StageType getStageType() {
        return StageType.HOST_CHECK;
    }
}
