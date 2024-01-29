package org.apache.bigtop.manager.server.job.stage;

import lombok.Setter;

@Setter
public abstract class AbstractStageLifecycle implements StageLifecycle {

    protected StageLifecycleContext context;

    @Override
    public void beforeStage() {

    }

    @Override
    public void onStageSuccess() {

    }

    @Override
    public void onStageFailed() {

    }
}
