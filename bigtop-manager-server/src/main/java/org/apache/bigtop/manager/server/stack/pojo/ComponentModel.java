package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentModel {

    private String name;

    @XmlElement(name = "display-name")
    private String displayName;

    private String category;

    private String cardinality;

    @XmlElement(name = "command-script")
    private ScriptModel commandScript;

    @XmlElementWrapper(name = "custom-commands")
    @XmlElements(@XmlElement(name = "custom-command"))
    private List<CustomCommandModel> customCommands;
}
