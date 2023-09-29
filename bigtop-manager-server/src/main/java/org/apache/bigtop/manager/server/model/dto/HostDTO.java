package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class HostDTO {

    private Long clusterId;

    private List<String> hostnames;
}
