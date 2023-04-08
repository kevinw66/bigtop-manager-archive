package org.apache.bigtop.manager.mpack.info;

import lombok.Data;

@Data
public class ServiceInfo {
    private String name;
    private String displayName;
    private String version;
    private String comment;
    private String serviceType;
    private Selection selection;
    private String maintainer;

    public enum Selection {
        DEFAULT,
        TECH_PREVIEW,
        MANDATORY,
        DEPRECATED
    }
}
