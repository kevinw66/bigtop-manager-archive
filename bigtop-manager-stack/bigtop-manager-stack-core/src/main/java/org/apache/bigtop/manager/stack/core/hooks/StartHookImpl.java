package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import org.apache.bigtop.manager.stack.core.ExecuteImpl;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@AutoService(Hook.class)
public class StartHookImpl implements Hook {


    @Override
    @HookAnnotation(before = "any")
    public void before() {
        System.out.println("before start");
    }

    @Override
    @HookAnnotation(after = "any")
    public void after() {
        System.out.println("after start");
    }

    @Override
    public String getName() {
        return "start";
    }
}
