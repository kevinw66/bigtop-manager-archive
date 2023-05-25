package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class AnyHookImpl implements Hook {


    @Override
    public void before() {
        log.info("before any");
    }

    @Override
    public void after() {
        log.info("after any");
    }

    @Override
    public String getName() {
        return "any";
    }
}
