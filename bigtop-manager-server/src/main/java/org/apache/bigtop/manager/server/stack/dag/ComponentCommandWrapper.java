package org.apache.bigtop.manager.server.stack.dag;

import lombok.*;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.orm.entity.Component;


@Data
@AllArgsConstructor
public class ComponentCommandWrapper {

    private String componentName;

    private Command command;

    private Component component;

    @Override
    public String toString() {
        return componentName + "-" + command.name();
    }
}
