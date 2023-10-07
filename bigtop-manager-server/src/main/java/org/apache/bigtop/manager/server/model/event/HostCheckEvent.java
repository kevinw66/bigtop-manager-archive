package org.apache.bigtop.manager.server.model.event;

import lombok.Data;

import java.util.List;

@Data
public class HostCheckEvent {

    private Long jobId;

    private List<String> hostnames;

}
