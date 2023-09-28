package org.apache.bigtop.manager.stack.common.enums;

import lombok.Getter;
import org.apache.bigtop.manager.common.enums.OSType;

import java.util.List;

import static org.apache.bigtop.manager.common.enums.OSType.*;

@Getter
public enum PackageManagerType {

    RPM(List.of(CENTOS7, ROCKY8, FEDORA36)),

    DEB(List.of(UBUNTU20, UBUNTU22, DEBIAN10, DEBIAN11)),

    ;

    /**
     * Supported OS Types for Package Manager
     */
    private final List<OSType> osTypes;

    PackageManagerType(List<OSType> osTypes) {
        this.osTypes = osTypes;
    }

}
