package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.spi.stack.Hook;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class RestartHookImpl implements Hook {

    @Override
    @HookAnnotation(before = HookType.START)
    public void before() {
    }

    @Override
    @HookAnnotation(after = HookType.START)
    public void after() {
    }

    @Override
    public String getName() {
        return HookType.RESTART.name();
    }
}
