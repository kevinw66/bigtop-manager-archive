package org.apache.bigtop.manager.server.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CommandIdentifier {

    private CommandLevel commandLevel;

    private Command command;
}
