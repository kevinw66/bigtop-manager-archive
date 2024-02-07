package org.apache.bigtop.manager.server.command.stage.factory.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.command.stage.factory.AbstractStageFactory;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceConfigureStageFactory extends AbstractStageFactory {

    @Override
    public StageType getStageType() {
        return StageType.SERVICE_CONFIGURE;
    }

    @Override
    public void doCreateStage() {
    }
}
