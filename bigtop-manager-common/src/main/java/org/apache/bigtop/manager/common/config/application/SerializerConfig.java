package org.apache.bigtop.manager.common.config.application;

import lombok.Data;

@Data
public class SerializerConfig {

    private String type = "kryo";
}
