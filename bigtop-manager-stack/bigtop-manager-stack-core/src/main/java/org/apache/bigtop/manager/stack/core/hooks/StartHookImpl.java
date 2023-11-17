package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class StartHookImpl implements Hook {

    @Override
    @HookAnnotation(before = HookType.ANY)
    public void before() {
    }

    @Override
    @HookAnnotation(after = HookType.ANY)
    public void after() {
    }

    @Override
    public String getName() {
        return HookType.START.name();
    }
}
