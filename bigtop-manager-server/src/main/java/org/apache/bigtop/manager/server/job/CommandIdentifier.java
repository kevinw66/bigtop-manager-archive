package org.apache.bigtop.manager.server.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;

import java.util.Objects;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CommandIdentifier {

    private CommandLevel commandLevel;

    private Command command;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandIdentifier that = (CommandIdentifier) o;
        return commandLevel == that.commandLevel && command == that.command;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandLevel, command);
    }
}
