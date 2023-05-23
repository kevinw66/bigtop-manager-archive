package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import org.apache.bigtop.manager.stack.spi.Hook;

/**
 * obtain agent execute command
 */
@AutoService(Hook.class)
public class InstallHookImpl implements Hook {


    @Override
    public void before() {

    }

    @Override
    public void after() {

    }

    @Override
    public String getName() {
        return "install";
    }
}
