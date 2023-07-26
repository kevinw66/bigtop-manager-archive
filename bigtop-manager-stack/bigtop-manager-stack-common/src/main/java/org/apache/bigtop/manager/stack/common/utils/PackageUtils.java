package org.apache.bigtop.manager.stack.common.utils;

import org.apache.bigtop.manager.common.enums.OSType;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.spi.PackageManager;
import org.apache.bigtop.manager.stack.spi.SPIFactory;

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
            return null;
        }

        String os = OSDetection.getOS();

        PackageManager packageManager = null;
        if (os.equalsIgnoreCase(OSType.CENTOS7.name())) {
            packageManager = packageManagerMap.get("rpm");
        }
        if (packageManager == null) {
            throw new StackException("Unsupported OS: " + os);
        }
        return packageManager.installPackage(packageList);
    }

}
