package org.apache.bigtop.manager.server.stack.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import org.apache.bigtop.manager.server.stack.pojo.PropertyModel;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@XmlRootElement(name="configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationXml {

    private String schemaVersion;

    @XmlAnyAttribute
    private Map<QName, String> attributes = new HashMap<>();

    @XmlElement(name="property")
    private List<PropertyModel> propertyModels;
}
