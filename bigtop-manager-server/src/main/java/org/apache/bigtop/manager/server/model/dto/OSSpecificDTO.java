package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class OSSpecificDTO {

    private List<String> os;

    private List<String> arch;

    private List<String> packages;
}
