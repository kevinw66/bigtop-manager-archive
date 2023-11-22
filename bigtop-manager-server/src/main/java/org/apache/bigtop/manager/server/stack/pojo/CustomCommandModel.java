package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomCommandModel {

    private String name;

    private ScriptModel commandScript;
}
