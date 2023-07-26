package org.apache.bigtop.manager.common.message.type.pojo;

import lombok.Data;

@Data
public class BasicInfo {

    private String javaHome;

    private String javaVersion;

    private String jdbcDriver;

    private String jdbcDriverHome;
}
