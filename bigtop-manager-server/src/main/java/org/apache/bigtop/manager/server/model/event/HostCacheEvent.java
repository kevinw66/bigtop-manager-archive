package org.apache.bigtop.manager.server.model.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class HostCacheEvent extends ApplicationEvent {

    Long jobId;

    public HostCacheEvent(Object source) {
        super(source);
    }
}
