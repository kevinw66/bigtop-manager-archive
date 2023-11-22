package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class StackModel {

    private String stackName;

    private String stackVersion;

    private String root;

    private String userGroup;

    private String packages;

    private String repoTemplate;

    @XmlElementWrapper(name="repos")
    @XmlElements(@XmlElement(name="repo"))
    private List<RepoModel> repos;
}
