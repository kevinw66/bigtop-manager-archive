package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class StackVO {

    private Long id;

    private String stackName;

    private String stackVersion;
}
