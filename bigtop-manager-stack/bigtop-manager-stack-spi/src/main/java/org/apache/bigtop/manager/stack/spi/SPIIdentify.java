package org.apache.bigtop.manager.stack.spi;

public interface SPIIdentify {
    default String getName(){
        return this.getClass().getName();
    }
}
