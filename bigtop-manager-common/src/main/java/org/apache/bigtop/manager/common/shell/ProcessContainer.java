package org.apache.bigtop.manager.common.shell;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * process manage container
 */
@Slf4j
public class ProcessContainer extends ConcurrentHashMap<Integer, Process> {

    private static final ProcessContainer container = new ProcessContainer();

    private ProcessContainer() {
        super();
    }

    public static ProcessContainer getInstance() {
        return container;
    }

    public static void putProcess(Process process) {
        getInstance().put(process.hashCode(), process);
    }

    public static int processSize() {
        return getInstance().size();
    }

    public static void removeProcess(Process process) {
        getInstance().remove(process.hashCode());
    }

    public static void destroyAllProcess() {
        Set<Entry<Integer, Process>> set = getInstance().entrySet();
        for (Entry<Integer, Process> entry : set) {
            try {
                entry.getValue().destroy();
            } catch (Exception e) {
                log.error("Destroy All Processes error", e);
            }
        }

        log.info("close " + set.size() + " executing process tasks");
    }
}
