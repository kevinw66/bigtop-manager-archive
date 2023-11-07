package org.apache.bigtop.manager.stack.common;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class BaseParams {

    public static final String limitsConfDir = "/etc/security/limits.d";

    /**
     * get the package list according to the os and arch
     */
    public static List<String> getPackageList(CommandPayload commandMessage) {
        List<OSSpecificInfo> osSpecifics = commandMessage.getOsSpecifics();
        if (osSpecifics == null) {
            return null;
        }

        String os = OSDetection.getOS();
        String arch = OSDetection.getArch();
        for (OSSpecificInfo osSpecific : osSpecifics) {
            List<String> pkgOS = osSpecific.getOs();
            List<String> pkgArch = osSpecific.getArch();
            if (pkgOS.contains(os) && pkgArch.contains(arch)) {
                return osSpecific.getPackages();
            }
        }

        return null;
    }

    /**
     * service home dir
     */
    public static String serviceHome(CommandPayload commandMessage) {
        String stackName = commandMessage.getStackName();
        String stackVersion = commandMessage.getStackVersion();
        String service = commandMessage.getServiceName();
        String root = commandMessage.getRoot();

        return root + "/" + stackName.toLowerCase() + "/" + stackVersion + "/usr/lib/" + service.toLowerCase();
    }

    /**
     * service conf dir
     */
    public static String confDir(CommandPayload commandMessage) {
        return "/etc/" + commandMessage.getServiceName().toLowerCase() + "/conf";
    }

    public static String user(CommandPayload commandMessage) {
        return StringUtils.isNotBlank(commandMessage.getServiceUser()) ? commandMessage.getServiceUser() : "root";
    }

    public static String group(CommandPayload commandMessage) {
        return StringUtils.isNotBlank(commandMessage.getServiceGroup()) ? commandMessage.getServiceGroup() : "root";
    }

    public static String serviceName(CommandPayload commandMessage) {
        return commandMessage.getServiceName();
    }

}
