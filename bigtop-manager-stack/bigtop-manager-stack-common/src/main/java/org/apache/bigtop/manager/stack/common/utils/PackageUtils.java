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
    private static final Map<String, PackageManager> PACKAGE_MANAGER_MAP;

    static {
        SPIFactory<PackageManager> spiFactory = new SPIFactory<>(PackageManager.class);
        PACKAGE_MANAGER_MAP = spiFactory.getSPIMap();
    }

    public static PackageManager getPackageManager() {
        String os = OSDetection.getOS();
        OSType currentOS;
        if (EnumUtils.isValidEnumIgnoreCase(OSType.class, os)) {
            currentOS = OSType.valueOf(os.toUpperCase());
        } else {
            throw new StackException("PackageManager Unsupported OS for [" + os + "]");
        }

        PackageManager packageManager = null;
        PackageManagerType[] values = PackageManagerType.values();
        for (PackageManagerType value : values) {
            List<OSType> osTypes = value.getOsTypes();
            if (osTypes.contains(currentOS)) {
                packageManager = PACKAGE_MANAGER_MAP.get(value.name());
                break;
            }
        }

        if (packageManager == null) {
            throw new StackException("Unsupported PackageManager for [" + os + "]");
        }
        return packageManager;
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

        return getPackageManager().installPackage(packageList);
    }

}
