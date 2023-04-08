package org.apache.bigtop.manager.spi.mpack;

public interface Script {
    void install();

    void configuration();

    void start();

    void stop();

    void status();
}
