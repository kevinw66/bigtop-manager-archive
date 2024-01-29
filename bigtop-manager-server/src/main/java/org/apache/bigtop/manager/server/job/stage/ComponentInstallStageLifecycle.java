package org.apache.bigtop.manager.server.job.stage;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentInstallStageLifecycle extends AbstractStageLifecycle {

    @Override
    public StageType getStageType() {
        return StageType.COMPONENT_INSTALL;
    }

    @Override
    public Stage createStage() {
        return null;
    }
}
