package org.apache.bigtop.manager.common.configuration.application;

import lombok.Data;

@Data
public class StackConfiguration {

    private String cacheDir;

    private String envFile;
}
