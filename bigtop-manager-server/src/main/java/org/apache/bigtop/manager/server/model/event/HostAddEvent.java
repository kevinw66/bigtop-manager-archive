package org.apache.bigtop.manager.server.model.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
public class HostAddEvent extends ApplicationEvent {

    Long jobId;

    List<String> hostnames;

    public HostAddEvent(Object source) {
        super(source);
    }
}
