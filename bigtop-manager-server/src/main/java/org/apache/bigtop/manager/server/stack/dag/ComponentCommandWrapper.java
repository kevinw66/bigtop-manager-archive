package org.apache.bigtop.manager.server.stack.dag;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.commons.text.CaseUtils;

import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentCommandWrapper that = (ComponentCommandWrapper) o;
        return Objects.equals(componentName, that.componentName) && command == that.command;
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentName, command);
    }

    public String toDisplayString() {
        return component.getDisplayName() + " " + CaseUtils.toCamelCase(command.name(), true);
    }
}
