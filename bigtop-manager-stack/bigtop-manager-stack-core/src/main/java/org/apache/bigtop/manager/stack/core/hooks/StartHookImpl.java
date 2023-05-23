package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@AutoService(Hook.class)
public class StartHookImpl implements Hook {


    @Override
    public void before() {
        System.out.println("before start");
    }

    @Override
    public void after() {
        System.out.println("after start");
    }

    @Override
    public String getName() {
        return "start";
    }
}
