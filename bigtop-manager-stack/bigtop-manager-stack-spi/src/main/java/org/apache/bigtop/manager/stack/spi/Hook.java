package org.apache.bigtop.manager.stack.spi;


public interface Hook extends SPIIdentify {

    void before();

    void after();

}
