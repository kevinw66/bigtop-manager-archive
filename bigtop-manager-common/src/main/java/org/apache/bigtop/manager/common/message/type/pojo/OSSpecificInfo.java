package org.apache.bigtop.manager.common.message.type.pojo;

import lombok.Data;

import java.util.List;

@Data
public class OSSpecificInfo {

    private List<String> os;

    private List<String> arch;

    private List<String> packages;
}
