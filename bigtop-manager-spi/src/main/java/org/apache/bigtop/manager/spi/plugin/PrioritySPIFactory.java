package org.apache.bigtop.manager.spi.plugin;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Slf4j
public class PrioritySPIFactory<T extends PrioritySPI> {

    private final Map<String, T> map = new HashMap<>();

    public PrioritySPIFactory(Class<T> spiClass) {
        for (T t : ServiceLoader.load(spiClass)) {
            if (map.containsKey(t.getName())) {
                resolveConflict(t);
            } else {
                map.put(t.getName(), t);
            }
        }
    }

    public Map<String, T> getSPIMap() {
        return Collections.unmodifiableMap(map);
    }

    private void resolveConflict(T newSPI) {
        T oldSPI = map.get(newSPI.getName());

        if (newSPI.compareTo(oldSPI.getPriority()) == 0) {
            throw new IllegalArgumentException(
                    String.format("These two spi plugins has conflict identify name with the same priority: %s, %s",
                            oldSPI, newSPI));
        } else if (newSPI.compareTo(oldSPI.getPriority()) > 0) {
            log.info("The {} plugin has high priority, will override {}", newSPI, oldSPI);
            map.put(newSPI.getName(), newSPI);
        } else {
            log.info("The low plugin {} will be skipped", newSPI);
        }
    }
}

