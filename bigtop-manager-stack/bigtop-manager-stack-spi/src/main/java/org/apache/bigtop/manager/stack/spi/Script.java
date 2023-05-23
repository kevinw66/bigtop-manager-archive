package org.apache.bigtop.manager.stack.spi;

public interface Script extends SPIIdentify {
    void install();

    void configuration();

    void start();

    void stop();

    void status();
}
