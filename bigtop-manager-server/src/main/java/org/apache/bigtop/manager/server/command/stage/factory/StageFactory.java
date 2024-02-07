package org.apache.bigtop.manager.server.command.stage.factory;

import org.apache.bigtop.manager.dao.entity.Stage;

/**
 * Stage factory.
 */
public interface StageFactory {

    /**
     * Gets the type of the stage.
     *
     * @return the type of the stage
     */
    StageType getStageType();

    /**
     * Creates a stage.
     *
     * @param context the stage context
     * @return the stage
     */
    Stage createStage(StageContext context);
}