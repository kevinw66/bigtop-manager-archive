package org.apache.bigtop.manager.server.command.stage.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.command.stage.factory.StageContext;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.dao.entity.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class StageRunners {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<StageType, String> STAGE_RUNNERS = new HashMap<>();

    public static StageRunner getStageRunner(Stage stage) {
        if (!LOADED.get()) {
            load();
        }

        StageContext stageContext = JsonUtils.readFromString(stage.getContext(), StageContext.class);
        StageType stageType = stageContext.getStageType();

        String beanName = STAGE_RUNNERS.get(stageType);
        StageRunner runner = SpringContextHolder.getApplicationContext().getBean(beanName, StageRunner.class);
        runner.setStage(stage);
        runner.setStageContext(stageContext);

        return runner;
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (Map.Entry<String, StageRunner> entry : SpringContextHolder.getStageRunners().entrySet()) {
            String beanName = entry.getKey();
            StageRunner stageRunner = entry.getValue();
            if (STAGE_RUNNERS.containsKey(stageRunner.getStageType())) {
                log.error("Duplicate StageLifecycle with type: {}", stageRunner.getStageType());
                continue;
            }

            STAGE_RUNNERS.put(stageRunner.getStageType(), beanName);
            log.info("Load StageLifecycle: {} with type: {}", stageRunner.getClass().getName(), stageRunner.getStageType());
        }

        LOADED.set(true);
    }
}
