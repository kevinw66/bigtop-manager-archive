package org.apache.bigtop.manager.server.config;

import org.apache.bigtop.manager.server.enums.CommandType;
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
            CommandType commandType = bean.getCommandType();

            switch (commandType) {
                case SERVICE:
                    defaultGroupSequence.add(ServiceCommandGroup.class);
                    break;
                case HOST:
                    defaultGroupSequence.add(HostCommandGroup.class);
                    break;
                case COMPONENT:
                    defaultGroupSequence.add(ComponentCommandGroup.class);
                    break;
                case SERVICE_INSTALL:
                    defaultGroupSequence.add(ServiceInstallCommandGroup.class);
                    break;
                case HOST_INSTALL:
                    defaultGroupSequence.add(HostInstallCommandGroup.class);
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
