package org.apache.bigtop.manager.server.model.dto.command;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HostCommandDTO implements Serializable {

    private List<String> componentNames;

    private String hostname;
}
