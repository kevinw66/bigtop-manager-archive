package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class StackModel {

    @XmlElement(name = "stack-name")
    private String stackName;

    @XmlElement(name = "stack-version")
    private String stackVersion;

    private String root;

    @XmlElement(name = "user-group")
    private String userGroup;

    private String packages;

    @XmlElement(name = "repo-template")
    private String repoTemplate;

    @XmlElementWrapper(name = "repos")
    @XmlElements(@XmlElement(name = "repo"))
    private List<RepoModel> repos;
}
