package org.apache.bigtop.manager.spi.stack;


import org.apache.bigtop.manager.spi.plugin.PrioritySPI;

public interface Hook extends PrioritySPI {

    void before();

    void after();
}
