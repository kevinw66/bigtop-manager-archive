package org.apache.bigtop.manager.server.stack.dag;

import lombok.*;
import org.apache.bigtop.manager.common.enums.Command;


@Data
@AllArgsConstructor
public class ComponentCommandWrapper {

    private String componentName;

    private Command command;

}
