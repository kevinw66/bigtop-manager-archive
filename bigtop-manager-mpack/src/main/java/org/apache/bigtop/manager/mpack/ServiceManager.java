package org.apache.bigtop.manager.mpack;

import org.apache.bigtop.manager.spi.plugin.SPIFactory;
import org.apache.bigtop.manager.spi.mpack.Script;

public class ServiceManager {
    public static void main(String[] args) {
        new SPIFactory().create(Script.class);
    }
}
