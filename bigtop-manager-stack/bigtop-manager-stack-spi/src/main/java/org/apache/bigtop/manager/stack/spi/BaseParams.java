package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class BaseParams {

    public static final String limitsConfDir = "/etc/security/limits.d";

    private final CommandPayload commandPayload;

    public BaseParams(CommandPayload commandPayload) {
        this.commandPayload = commandPayload;
    }

    /**
     * get the package list according to the os and arch
     */
    public List<String> getPackageList() {
        List<OSSpecificInfo> osSpecifics = this.commandPayload.getOsSpecifics();
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
    public String serviceHome() {
        String stackName = this.commandPayload.getStackName();
        String stackVersion = this.commandPayload.getStackVersion();
        String service = this.commandPayload.getServiceName();
        String root = this.commandPayload.getRoot();

        return root + "/" + stackName.toLowerCase() + "/" + stackVersion + "/usr/lib/" + service.toLowerCase();
    }

    /**
     * service conf dir
     */
    public String confDir() {
        return "/etc/" + this.commandPayload.getServiceName().toLowerCase() + "/conf";
    }

    public String user() {
        return StringUtils.isNotBlank(this.commandPayload.getServiceUser()) ? this.commandPayload.getServiceUser() : "root";
    }

    public String group() {
        return StringUtils.isNotBlank(this.commandPayload.getServiceGroup()) ? this.commandPayload.getServiceGroup() : "root";
    }

    public String serviceName() {
        return this.commandPayload.getServiceName();
    }

}
