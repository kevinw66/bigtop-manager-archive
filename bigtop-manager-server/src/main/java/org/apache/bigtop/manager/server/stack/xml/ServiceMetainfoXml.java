package org.apache.bigtop.manager.server.stack.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;

@Data
@XmlRootElement(name="metainfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceMetainfoXml {

    private String schemaVersion;

    private ServiceModel service;
}
