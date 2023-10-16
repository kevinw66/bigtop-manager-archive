package org.apache.bigtop.manager.server.statemachine;

import org.springframework.stereotype.Component;

@Component
public class InstallEvent implements Event {
    @Override
    public String execute() {
        return "install";
    }
}
