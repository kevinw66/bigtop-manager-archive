package org.apache.bigtop.manager.spi.mpack;

import org.apache.bigtop.manager.spi.plugin.SPIIdentify;

public interface Script extends SPIIdentify {
    void install();

    void configuration();

    void start();

    void stop();

    void status();
}
