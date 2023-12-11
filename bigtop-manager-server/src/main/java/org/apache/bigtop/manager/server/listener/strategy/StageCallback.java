package org.apache.bigtop.manager.server.listener.strategy;

import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;

public interface StageCallback {
    default void onStageCompleted(Stage stage) {
    }

    default String generatePayload(Task task) {
        return task.getContent();
    }
}
