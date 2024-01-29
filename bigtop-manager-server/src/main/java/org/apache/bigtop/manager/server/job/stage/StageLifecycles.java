package org.apache.bigtop.manager.server.job.stage;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class StageLifecycles {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<StageType, String> STAGE_LIFECYCLES = new HashMap<>();

    public static StageLifecycle getStageLifecycle(StageType stageType) {
        if (!LOADED.get()) {
            load();
        }

        String beanName = STAGE_LIFECYCLES.get(stageType);
        return SpringContextHolder.getApplicationContext().getBean(beanName, StageLifecycle.class);
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (Map.Entry<String, StageLifecycle> entry : SpringContextHolder.getStageLifecycles().entrySet()) {
            String beanName = entry.getKey();
            StageLifecycle stageLifecycle = entry.getValue();
            if (STAGE_LIFECYCLES.containsKey(stageLifecycle.getStageType())) {
                log.error("Duplicate StageLifecycle with type: {}", stageLifecycle.getStageType());
                continue;
            }

            STAGE_LIFECYCLES.put(stageLifecycle.getStageType(), beanName);
            log.info("Load StageLifecycle: {} with type: {}", stageLifecycle.getClass().getName(), stageLifecycle.getStageType());
        }

        LOADED.set(true);
    }
}
