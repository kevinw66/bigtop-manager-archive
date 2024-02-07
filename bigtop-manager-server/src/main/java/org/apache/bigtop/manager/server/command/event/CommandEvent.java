package org.apache.bigtop.manager.server.command.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CommandEvent extends ApplicationEvent {

    private Long jobId;

    public CommandEvent(Object source) {
        super(source);
    }
}
