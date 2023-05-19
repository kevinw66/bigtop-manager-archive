package org.apache.bigtop.manager.common.configuration.application;

import lombok.Data;

@Data
public class SerializerConfiguration {

    private String type = "kryo";
}
