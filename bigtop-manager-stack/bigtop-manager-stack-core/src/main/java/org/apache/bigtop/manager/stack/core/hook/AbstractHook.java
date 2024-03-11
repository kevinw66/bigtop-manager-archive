package org.apache.bigtop.manager.stack.core.hook;


import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.spi.stack.Hook;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxAccountUtils;

import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class AbstractHook implements Hook {

    @Override
    public void before() {
        addUserAndGroup();

        doBefore();
    }

    @Override
    public void after() {
        doAfter();
    }

    protected abstract void doBefore();

    protected abstract void doAfter();

    private void addUserAndGroup() {
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
}
