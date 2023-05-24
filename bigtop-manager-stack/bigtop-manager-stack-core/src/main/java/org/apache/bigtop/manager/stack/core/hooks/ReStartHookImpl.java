package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import org.apache.bigtop.manager.stack.core.ExecuteImpl;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@AutoService(Hook.class)
public class ReStartHookImpl implements Hook {


    @Override
    @HookAnnotation(before = "start")
    public void before() {
        System.out.println("before restart");
    }

    @Override
    @HookAnnotation(after = "start")
    public void after() {
        System.out.println("after restart");
    }

    @Override
    public String getName() {
        return "restart";
    }
}
