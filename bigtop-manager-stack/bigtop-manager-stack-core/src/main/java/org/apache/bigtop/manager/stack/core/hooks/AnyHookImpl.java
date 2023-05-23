package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@AutoService(Hook.class)
public class AnyHookImpl implements Hook {


    @Override
    public void before() {
        System.out.println("before any");
    }

    @Override
    public void after() {
        System.out.println("after any");
    }

    @Override
    public String getName() {
        return "any";
    }
}
