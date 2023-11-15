package org.apache.bigtop.manager.server.stack.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;

import java.util.List;

@Data
@XmlRootElement(name="metainfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceMetainfoXml {

    private String schemaVersion;

    @XmlElementWrapper(name="services")
    @XmlElements(@XmlElement(name="service"))
    private List<ServiceModel> services;
}
