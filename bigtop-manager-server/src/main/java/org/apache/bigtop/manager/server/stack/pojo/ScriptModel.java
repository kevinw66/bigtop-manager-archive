package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptModel {

    @XmlElement(name = "script-type")
    private String scriptType;

    @XmlElement(name = "script")
    private String scriptId;

    private Long timeout;
}
