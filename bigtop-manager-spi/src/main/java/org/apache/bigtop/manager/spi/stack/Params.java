package org.apache.bigtop.manager.spi.stack;

import java.util.List;

public interface Params {

    List<String> getPackageList();

    String serviceHome();

    String confDir();

    String user();

    String group();

    String serviceName();
}

