package org.apache.bigtop.manager.stack.core.hook;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.spi.stack.Hook;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class RestartHook extends AbstractHook {

    public static final String NAME = "restart";

    @Override
    public void doBefore() {
    }

    @Override
    public void doAfter() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
