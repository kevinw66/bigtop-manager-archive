package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class StackVO {

    private String stackName;

    private String stackVersion;

    private List<StackServiceVO> services;

    private List<StackRepoVO> repos;
}
