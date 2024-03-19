package org.apache.bigtop.manager.spi.stack;

import org.apache.bigtop.manager.common.shell.ShellResult;
import org.apache.bigtop.manager.spi.plugin.PrioritySPI;

import java.util.Collection;

public interface PackageManager extends PrioritySPI {

    ShellResult installPackage(Collection<String> packages);

    ShellResult uninstallPackage(Collection<String> packages);

    String listPackages();

}
