package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceModel {

    private String name;

    @XmlElement(name = "display-name")
    private String displayName;

    private String desc;

    private String version;

    private String user;

    private String group;

    @XmlElementWrapper(name = "os-specifics")
    @XmlElements(@XmlElement(name = "os-specific"))
    private List<OSSpecificModel> osSpecifics;

    @XmlElementWrapper(name = "components")
    @XmlElements(@XmlElement(name = "component"))
    private List<ComponentModel> components;

    @XmlElementWrapper(name = "required-services")
    @XmlElements(@XmlElement(name = "service"))
    private List<String> requiredServices;
}
