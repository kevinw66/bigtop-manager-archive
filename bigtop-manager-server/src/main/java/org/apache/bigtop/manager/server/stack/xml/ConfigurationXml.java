package org.apache.bigtop.manager.server.stack.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import org.apache.bigtop.manager.server.stack.pojo.PropertyModel;

import java.util.List;

@Data
@XmlRootElement(name="configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationXml {

    private String schemaVersion;

    @XmlElement(name="property")
    private List<PropertyModel> propertyModels;
}
