package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyModel {

    private String name;

    private String value;

    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement(name = "description")
    private String desc;

}
