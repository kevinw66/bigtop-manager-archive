package org.apache.bigtop.manager.server.statemachine;

import org.springframework.stereotype.Component;

@Component
public class StartEvent implements Event {
    @Override
    public String execute() {
        return "start";
    }
}
