package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentModel {

    private String name;

    private String displayName;

    private String category;

    private String cardinality;

    private ScriptModel commandScript;

    @XmlElementWrapper(name="customCommands")
    @XmlElements(@XmlElement(name="customCommand"))
    private List<CustomCommandModel> customCommands;
}
