package org.apache.bigtop.manager.stack.common.utils;

import org.apache.bigtop.manager.common.enums.OSType;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.enums.PackageManagerType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.spi.PackageManager;
import org.apache.bigtop.manager.stack.spi.SPIFactory;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PackageUtils {
    private static final Map<String, PackageManager> packageManagerMap;

    static {
        SPIFactory<PackageManager> spiFactory = new SPIFactory<>(PackageManager.class);
        packageManagerMap = spiFactory.getSPIMap();
    }

    /**
     * install package
     *
     * @param packageList packages need to be installed
     */
    public static ShellResult install(Collection<String> packageList) {
        if (packageList == null || packageList.isEmpty()) {
            ShellResult shellResult = new ShellResult();
            shellResult.setExitCode(-1);
            shellResult.setErrMsg("packageList is empty");
            return shellResult;
        }
        List<OSType> rpmOsTypes = PackageManagerType.RPM.getOsTypes();
        List<OSType> debOsTypes = PackageManagerType.DEB.getOsTypes();

        String os = OSDetection.getOS();
        OSType currentOS;
        if (EnumUtils.isValidEnumIgnoreCase(OSType.class, os)) {
            currentOS = OSType.valueOf(os.toUpperCase());
        } else {
            throw new StackException("PackageManager Unsupported OS for [" + os + "]");
        }

        PackageManager packageManager = null;
        if (rpmOsTypes.contains(currentOS)) {
            packageManager = packageManagerMap.get(PackageManagerType.RPM.name());
        } else if (debOsTypes.contains(currentOS)) {
            packageManager = packageManagerMap.get(PackageManagerType.DEB.name());
            // todo implement DEB package manager
            throw new StackException("PackageManager for DEB is not implemented yet");
        }

        if (packageManager == null) {
            throw new StackException("Unsupported PackageManager for [" + os + "]");
        }

        return packageManager.installPackage(packageList);
    }

}
