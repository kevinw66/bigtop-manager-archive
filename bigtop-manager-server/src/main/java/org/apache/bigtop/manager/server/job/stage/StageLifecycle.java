package org.apache.bigtop.manager.server.job.stage;

import org.apache.bigtop.manager.server.orm.entity.Stage;

/**
 * The StageLifecycle interface defines the lifecycle of a stage in a job.
 * It provides methods for creating a stage, performing actions before the stage,
 * handling successful completion of the stage, and handling failure of the stage.
 */
public interface StageLifecycle {

    /**
     * Gets the type of the stage.
     *
     * @return the type of the stage
     */
    StageType getStageType();

    /**
     * Creates a new Stage.
     *
     * @return a new Stage instance
     */
    Stage createStage();

    /**
     * Performs actions before the stage starts.
     */
    void beforeStage();

    /**
     * Handles the successful completion of the stage.
     */
    void onStageSuccess();

    /**
     * Handles the failure of the stage.
     */
    void onStageFailed();
}