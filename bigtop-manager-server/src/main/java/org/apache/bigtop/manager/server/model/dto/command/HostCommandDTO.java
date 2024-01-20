package org.apache.bigtop.manager.server.model.dto.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class HostCommandDTO implements Serializable {

    private String hostname;
}
