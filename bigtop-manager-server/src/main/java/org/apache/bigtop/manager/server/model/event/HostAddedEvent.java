package org.apache.bigtop.manager.server.model.event;

import lombok.Data;

import java.util.List;

@Data
public class HostAddedEvent {

    private List<String> hostnames;
}
