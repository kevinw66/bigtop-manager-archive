package org.apache.bigtop.manager.server.config;

import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.model.req.CommandReq;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class CommandGroupSequenceProvider implements DefaultGroupSequenceProvider<CommandReq> {
    @Override
    public List<Class<?>> getValidationGroups(CommandReq bean) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(CommandReq.class); // 这一步不能省,否则Default分组都不会执行了，会抛错的

        if (bean != null) { // 这块判空请务必要做
            CommandLevel commandLevel = bean.getCommandLevel();

            switch (commandLevel) {
                case SERVICE:
                    if (bean.getCommand() == Command.INSTALL) {
                        defaultGroupSequence.add(ServiceInstallCommandGroup.class);
                    } else {
                        defaultGroupSequence.add(ServiceCommandGroup.class);
                    }
                    break;
                case HOST:
                    if (bean.getCommand() == Command.INSTALL) {
                        defaultGroupSequence.add(HostInstallCommandGroup.class);
                    } else {
                        defaultGroupSequence.add(HostCommandGroup.class);
                    }
                    break;
                case COMPONENT:
                    defaultGroupSequence.add(ComponentCommandGroup.class);
                    break;
            }

        }
        return defaultGroupSequence;
    }

    public interface ServiceCommandGroup {
    }

    public interface HostCommandGroup {
    }

    public interface ComponentCommandGroup {
    }

    public interface ServiceInstallCommandGroup {
    }

    public interface HostInstallCommandGroup {
    }
}
