package org.apache.bigtop.manager.server.stack.pojo;

import lombok.Data;

import java.util.List;

@Data
public class OSSpecificModel {

    private List<String> os;

    private List<String> arch;

    private List<String> packages;
}
