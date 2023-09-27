package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxAccountUtils;
import org.apache.bigtop.manager.stack.spi.Hook;

import java.util.Map;
import java.util.Set;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class AnyHookImpl implements Hook {

    @Override
    public void before() {
        log.info("before any");
        Map<String, Set<String>> users = LocalSettings.users();
        String userGroup = LocalSettings.cluster().getUserGroup();

        for (Map.Entry<String, Set<String>> user : users.entrySet()) {
            Set<String> groups = user.getValue();
            log.info("user: {} , groups: {}", user.getKey(), user.getValue());
            for (String group : groups) {
                LinuxAccountUtils.groupAdd(group);
            }
            LinuxAccountUtils.userAdd(user.getKey(), userGroup, groups);
        }
    }

    @Override
    public void after() {
        log.info("after any");
    }

    @Override
    public String getName() {
        return HookType.ANY.name();
    }
}
