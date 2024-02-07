package org.apache.bigtop.manager.server.command.stage.factory;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.dao.entity.Stage;

public abstract class AbstractStageFactory implements StageFactory {

    protected StageContext context;

    protected Stage stage;

    @Override
    public Stage createStage(StageContext context) {
        this.context = context;
        this.context.setStageType(getStageType());

        this.stage = new Stage();
        this.stage.setContext(JsonUtils.writeAsString(context));

        doCreateStage();

        return this.stage;
    }

    protected abstract void doCreateStage();
}
