package org.apache.bigtop.manager.server.job.strategy;

import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.entity.Task;

public interface StageCallback {

    default void beforeStage(Stage stage) {
    }

    default void afterStage(Stage stage) {
    }

    default String generatePayload(Task task) {
        return task.getContent();
    }
}
