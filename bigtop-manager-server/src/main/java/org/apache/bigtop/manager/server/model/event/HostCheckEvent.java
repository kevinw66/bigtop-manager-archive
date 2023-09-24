package org.apache.bigtop.manager.server.model.event;

import lombok.Data;

@Data
public class HostCheckEvent {

    private Long jobId;

    private String hostname;

}
