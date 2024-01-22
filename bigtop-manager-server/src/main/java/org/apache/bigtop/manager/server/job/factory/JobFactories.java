package org.apache.bigtop.manager.server.job.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.job.CommandIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class JobFactories {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<CommandIdentifier, JobFactory> JOB_FACTORIES = new HashMap<>();

    public static JobFactory getJobFactory(CommandIdentifier identifier) {
        if (!LOADED.get()) {
            load();
        }
        JobFactory jobFactory = JOB_FACTORIES.get(identifier);
        if (jobFactory == null) {
            throw new ApiException(ApiExceptionEnum.COMMAND_NOT_SUPPORTED,
                    identifier.getCommand().toLowerCase(), identifier.getCommandLevel().toLowerCase());
        }
        return jobFactory;
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (JobFactory jobFactory : SpringContextHolder.getJobFactories().values()) {
            JOB_FACTORIES.put(jobFactory.getCommandIdentifier(), jobFactory);
            log.info("Load JobFactory: {} with identifier: {}", jobFactory.getClass().getName(), jobFactory.getCommandIdentifier());
        }

        LOADED.set(true);
    }
}
