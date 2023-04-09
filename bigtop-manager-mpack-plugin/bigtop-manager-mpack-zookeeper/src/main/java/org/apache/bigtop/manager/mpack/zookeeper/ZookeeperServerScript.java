package org.apache.bigtop.manager.mpack.zookeeper;


import com.google.auto.service.AutoService;
import org.apache.bigtop.manager.spi.mpack.Script;

@AutoService(Script.class)
public class ZookeeperServerScript implements Script {
    @Override
    public void install() {
        System.out.println("install");
    }

    @Override
    public void configuration() {
        System.out.println("configuration");
    }

    @Override
    public void start() {
        System.out.println("start");
    }

    @Override
    public void stop() {
        System.out.println("stop");
    }

    @Override
    public void status() {
        System.out.println("status");
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
