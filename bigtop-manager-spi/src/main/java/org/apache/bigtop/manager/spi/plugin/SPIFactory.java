package org.apache.bigtop.manager.spi.plugin;

import org.apache.bigtop.manager.spi.mpack.Script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class SPIFactory<T extends Script> {

    private final Map<String, T> map = new HashMap<>();

    public void create(Class<T> spiClass) {
        for (T t : ServiceLoader.load(spiClass)) {
            System.out.println(t.getClass().getName());
            System.out.println(t.getName());
            map.put(t.getName(), t);
        }
    }

    public Map<String, T> getSPIMap() {
        return Collections.unmodifiableMap(map);
    }
}
