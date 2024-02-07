package org.apache.bigtop.manager.server.command.stage.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class StageFactories {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<StageType, String> STAGE_FACTORIES = new HashMap<>();

    public static StageFactory getStageFactory(StageType stageType) {
        if (!LOADED.get()) {
            load();
        }

        String beanName = STAGE_FACTORIES.get(stageType);
        return SpringContextHolder.getApplicationContext().getBean(beanName, StageFactory.class);
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (Map.Entry<String, StageFactory> entry : SpringContextHolder.getStageFactories().entrySet()) {
            String beanName = entry.getKey();
            StageFactory stageFactory = entry.getValue();
            if (STAGE_FACTORIES.containsKey(stageFactory.getStageType())) {
                log.error("Duplicate StageLifecycle with type: {}", stageFactory.getStageType());
                continue;
            }

            STAGE_FACTORIES.put(stageFactory.getStageType(), beanName);
            log.info("Load StageLifecycle: {} with type: {}", stageFactory.getClass().getName(), stageFactory.getStageType());
        }

        LOADED.set(true);
    }
}
