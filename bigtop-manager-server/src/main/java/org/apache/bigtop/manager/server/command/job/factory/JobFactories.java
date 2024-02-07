package org.apache.bigtop.manager.server.command.job.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.command.CommandIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class JobFactories {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<CommandIdentifier, String> JOB_FACTORIES = new HashMap<>();

    public static JobFactory getJobFactory(CommandIdentifier identifier) {
        if (!LOADED.get()) {
            load();
        }

        if (!JOB_FACTORIES.containsKey(identifier)) {
            throw new ApiException(ApiExceptionEnum.COMMAND_NOT_SUPPORTED, identifier.getCommand().toLowerCase(), identifier.getCommandLevel().toLowerCase());
        }

        String beanName = JOB_FACTORIES.get(identifier);
        return SpringContextHolder.getApplicationContext().getBean(beanName, JobFactory.class);
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (Map.Entry<String, JobFactory> entry : SpringContextHolder.getJobFactories().entrySet()) {
            String beanName = entry.getKey();
            JobFactory jobFactory = entry.getValue();
            if (JOB_FACTORIES.containsKey(jobFactory.getCommandIdentifier())) {
                log.error("Duplicate JobFactory with identifier: {}", jobFactory.getCommandIdentifier());
                continue;
            }

            JOB_FACTORIES.put(jobFactory.getCommandIdentifier(), beanName);
            log.info("Load JobFactory: {} with identifier: {}", jobFactory.getClass().getName(), jobFactory.getCommandIdentifier());
        }

        LOADED.set(true);
    }
}
