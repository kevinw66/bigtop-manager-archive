package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.common.annotations.HookGuard;
import org.apache.bigtop.manager.spi.stack.Hook;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class StopHookImpl implements Hook {

    @Override
    @HookGuard(before = HookType.ANY)
    public void before() {
    }

    @Override
    @HookGuard(after = HookType.ANY)
    public void after() {
    }

    @Override
    public String getName() {
        return HookType.STOP.name();
    }
}
