package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class RestartHookImpl implements Hook {


    @Override
    @HookAnnotation(before = "start")
    public void before() {
        log.info("before restart");
    }

    @Override
    @HookAnnotation(after = "start")
    public void after() {
        log.info("after restart");
    }

    @Override
    public String getName() {
        return "restart";
    }
}
