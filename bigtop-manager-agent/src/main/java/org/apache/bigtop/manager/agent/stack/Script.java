package org.apache.bigtop.manager.agent.stack;


public interface Script {
    void install();

    void configuration();

    void start();

    void stop();

    void status();

}
