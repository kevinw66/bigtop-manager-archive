package org.apache.bigtop.manager.server.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class HostCheckEvent extends Event {

    private Long jobId;

    private List<String> hostnames;

}
