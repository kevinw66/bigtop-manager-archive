package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.utils.shell.ShellResult;

import java.util.List;

public interface PackageManager extends SPIIdentify {

    ShellResult installPackage(List<String> packages);

    ShellResult uninstallPackage(List<String> packages);

    String listPackages();

}
