package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.utils.shell.ShellResult;

import java.util.Collection;
import java.util.List;

public interface PackageManager extends SPIIdentify {

    ShellResult installPackage(Collection<String> packages);

    ShellResult uninstallPackage(Collection<String> packages);

    String listPackages();

}
