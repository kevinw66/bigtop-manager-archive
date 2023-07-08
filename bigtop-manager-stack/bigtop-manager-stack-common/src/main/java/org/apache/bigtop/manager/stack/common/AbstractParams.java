package org.apache.bigtop.manager.stack.common;

import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecific;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class AbstractParams {

    public static CommandMessage commandMessage;

    /**
     * get the package list according to the os and arch
     *
     * @return
     */
    public static List<String> getPackageList() {
        List<OSSpecific> osSpecifics = commandMessage.getOsSpecifics();
        if (osSpecifics == null) {
            return null;
        }

        String os = OSDetection.getOS();
        String arch = OSDetection.getArch();
        for (OSSpecific osSpecific : osSpecifics) {
            List<String> pkgOS = osSpecific.getOs();
            List<String> pkgArch = osSpecific.getArch();
            if (pkgOS.contains(os) && pkgArch.contains(arch)) {
                return osSpecific.getPackages();
            }
        }

        return null;
    }

    /**
     * service cache dir
     *
     * @return
     */
    public static String serviceCacheDir() {
        String stack = commandMessage.getStack();
        String version = commandMessage.getVersion();
        String service = commandMessage.getService();
        String cacheDir = commandMessage.getCacheDir();
        return StringUtils.join(cacheDir + "/stacks/", stack.toUpperCase(), "/", version, "/services/", service.toUpperCase());
    }

    /**
     * service home dir
     *
     * @return
     */
    public static String serviceHome() {
        String stack = commandMessage.getStack();
        String version = commandMessage.getVersion();
        String service = commandMessage.getService();
        String root = commandMessage.getRoot();

        return root + "/" + version + "/usr/lib/" + service.toLowerCase();
    }

    /**
     * service conf dir
     *
     * @return
     */
    public static String confDir() {
        return "/etc/" + commandMessage.getService() + "/conf";
    }

}
