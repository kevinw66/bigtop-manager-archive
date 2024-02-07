package org.apache.bigtop.manager.server.command.stage.runner;

import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.entity.Task;

public interface StageRunner {

    StageType getStageType();

    void setStage(Stage stage);

    void beforeRun();

    void run();

    void onSuccess();

    void onFailure();

    void beforeRunTask(Task task);

    void onTaskSuccess(Task task);

    void onTaskFailure(Task task);
}
