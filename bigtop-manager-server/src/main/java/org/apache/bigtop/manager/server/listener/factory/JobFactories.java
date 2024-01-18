package org.apache.bigtop.manager.server.listener.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class JobFactories {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<CommandLevel, JobFactory> JOB_FACTORIES = new HashMap<>();

    public static JobFactory getJobFactory(CommandLevel commandLevel) {
        if (!LOADED.get()) {
            load();
        }

        return JOB_FACTORIES.get(commandLevel);
    }

    private static void load() {
        if (LOADED.get()) {
            return;
        }

        for (JobFactory jobFactory : SpringContextHolder.getJobFactory().values()) {
            JOB_FACTORIES.put(jobFactory.getCommandLevel(), jobFactory);
            log.info("Load JobFactory: {} with level: {}", jobFactory.getClass().getName(), jobFactory.getCommandLevel());
        }

        LOADED.set(true);
    }
}
