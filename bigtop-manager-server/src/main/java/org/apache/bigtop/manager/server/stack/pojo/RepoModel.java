package org.apache.bigtop.manager.server.stack.pojo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class RepoModel {

    @XmlElement(name = "repo-id")
    private String repoId;

    @XmlElement(name = "repo-name")
    private String repoName;

    @XmlElement(name = "base-url")
    private String baseUrl;

    private String os;

    private String arch;

}
