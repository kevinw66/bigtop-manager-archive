package org.apache.bigtop.manager.stack.spi;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Slf4j
public class SPIFactory<T extends SPIIdentify> {
    private final Map<String, T> map = new HashMap<>();

    public SPIFactory(Class<T> spiClass) {
        for (T t : ServiceLoader.load(spiClass)) {
            log.info("spiClass.getName: {}", t.getName());
            System.out.println(t.getName());
            map.put(t.getName(), t);
        }
    }

    public Map<String, T> getSPIMap() {
        return Collections.unmodifiableMap(map);
    }
}

