package org.apache.bigtop.manager.stack.spi;

public interface Script extends SPIIdentify {
    void install();

    void configuration();

    default void start() {
    }

    default void stop() {
    }

    void status();
}
