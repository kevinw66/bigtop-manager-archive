package org.apache.bigtop.manager.stack.spi;

import java.util.List;

public interface PackageManager extends SPIIdentify {

    void installPackage(List<String> packages);

    void uninstallPackage(List<String> packages);

    String listPackages();

}
