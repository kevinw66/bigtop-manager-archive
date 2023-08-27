package org.apache.bigtop.manager.server.stack.dag;

import lombok.*;


@Data
@AllArgsConstructor
public class ComponentCommandWrapper {

    private String componentName;

    private String command;

}
