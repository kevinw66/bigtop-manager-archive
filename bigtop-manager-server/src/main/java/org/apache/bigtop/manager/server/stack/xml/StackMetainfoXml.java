package org.apache.bigtop.manager.server.stack.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import org.apache.bigtop.manager.server.stack.pojo.StackModel;

@Data
@XmlRootElement(name="metainfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class StackMetainfoXml {

    @XmlElement(name="schema-version")
    private String schemaVersion;

    @XmlElement(name="stack")
    private StackModel stack;
}
